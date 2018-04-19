package com.chiricker.areas.chiricks.services.timeline;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.view.ChirickViewModel;
import com.chiricker.areas.chiricks.models.view.TimelinePostViewModel;
import com.chiricker.areas.chiricks.repositories.TimelinePostRepository;
import com.chiricker.areas.chiricks.repositories.TimelineRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.areas.chiricks.services.timelinePost.TimelinePostService;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.services.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class TimelineServiceImpl implements TimelineService {

    private final TimelineRepository timelineRepository;
    private final UserService userService;
    private final ChirickService chirickService;
    private final ModelMapper mapper;
    private final TimelinePostService timelinePostService;

    @Autowired
    public TimelineServiceImpl(TimelineRepository timelineRepository, UserService userService, ChirickService chirickService, ModelMapper mapper, TimelinePostService timelinePostService) {
        this.timelineRepository = timelineRepository;
        this.userService = userService;
        this.chirickService = chirickService;
        this.mapper = mapper;
        this.timelinePostService = timelinePostService;
    }

    private Chirick getChirick(String chirickId) {
        ChirickServiceModel serviceModel = this.chirickService.getById(chirickId);
        if (serviceModel == null) return null;
        return this.mapper.map(serviceModel, Chirick.class);
    }

    private User getUserWithHandle(String handle) throws UserNotFoundException {
        UserServiceModel userModel = this.userService.getByHandle(handle);
        if (userModel == null) throw new UserNotFoundException("No user with " + handle + " was found");
        return this.mapper.map(userModel, User.class);
    }

    private TimelinePostViewModel mapPostToViewModel(TimelinePostServiceModel post, User user) {
        TimelinePostViewModel viewModel = this.mapper.map(post, TimelinePostViewModel.class);

        ChirickViewModel chirickModel = viewModel.getChirick();
        Chirick chirick = this.mapper.map(post.getChirick(), Chirick.class);

        chirickModel.setRechiricksSize(chirick.getRechiricks().size());
        chirickModel.setRechiricked(chirick.getRechiricks().stream()
                .anyMatch(u -> u.getId().equals(user.getId())));

        chirickModel.setLikesSize(chirick.getLikes().size());
        chirickModel.setLiked(chirick.getLikes().stream()
                .anyMatch(u -> u.getId().equals(user.getId())));

        chirickModel.setCommentsSize(chirick.getComments().size());
        chirickModel.setCommented(chirick.getComments().stream()
                .anyMatch(u -> u.getUser().getId().equals(user.getId())));

        if (chirick.getParent() != null) {
            Chirick parent = chirick.getParent();
            String parentUrl = "/@" + parent.getUser().getHandle() + "/" + parent.getId();
            chirickModel.setParentUrl(parentUrl);
        }

        return viewModel;
    }

    @Override
    public List<TimelinePostViewModel> getTimelineForUser(String userHandle, Pageable pageable) throws UserNotFoundException {
        User user = this.getUserWithHandle(userHandle);

        Set<TimelinePost> posts = user.getTimeline().getPosts();
        if (posts.size() < 1) return new ArrayList<>();

        Page<TimelinePostViewModel> mappedPosts = this.timelinePostService.getPostsInCollection(posts, pageable)
                .map(p -> mapPostToViewModel(p, user));

        return mappedPosts.getContent();
    }

    @Async
    @Override
    @Transactional
    public Future updateTimeline(String userHandle, String chirickId, TimelinePostType type, boolean isAdding) throws UserNotFoundException {
        Chirick chirick = this.getChirick(chirickId);
        if (chirick == null) return null;

        User user = this.getUserWithHandle(userHandle);

        Set<Timeline> timelines = user
                .getFollowers()
                .stream()
                .map(User::getTimeline)
                .collect(Collectors.toSet());

        String userId = user.getId();
        for (Timeline timeline : timelines) {
            if (isAdding) {
                TimelinePost timelinePost = new TimelinePost();
                timelinePost.setChirick(chirick);
                timelinePost.setUser(user);
                timelinePost.setPostType(type);

                timeline.getPosts().add(timelinePost);

            } else {
                timeline
                        .getPosts()
                        .stream()
                        .filter(p -> p.getChirick().getId().equals(chirickId)
                                && p.getUser().getId().equals(userId)
                                && p.getPostType() == type)
                        .findFirst()
                        .ifPresent(p -> timeline.getPosts().remove(p));

            }

            this.timelineRepository.saveAndFlush(timeline);
        }

        return new AsyncResult(timelines);
    }

    @Async
    @Override
    @Transactional
    public Future unfollow(String userHandle, String requesterHandle) throws UserNotFoundException {
        User user = this.getUserWithHandle(userHandle);
        User requester = this.getUserWithHandle(requesterHandle);

        Timeline requesterTimeline = requester.getTimeline();

        Set<TimelinePost> posts = requesterTimeline.getPosts();
        Set<TimelinePost> postsToDelete = new HashSet<>();

        String userId = user.getId();
        for (TimelinePost post : posts) {
            if (post.getUser().getId().equals(userId)) {
                postsToDelete.add(post);
            }
        }

        requesterTimeline.getPosts().removeAll(postsToDelete);

        requesterTimeline = this.timelineRepository.save(requesterTimeline);

        return new AsyncResult(requesterTimeline);
    }
}