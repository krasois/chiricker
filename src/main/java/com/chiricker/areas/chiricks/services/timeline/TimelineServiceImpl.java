package com.chiricker.areas.chiricks.services.timeline;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.view.ChirickViewModel;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.view.TimelinePostViewModel;
import com.chiricker.areas.chiricks.repositories.TimelineRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.services.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public TimelineServiceImpl(TimelineRepository timelineRepository, UserService userService, ChirickService chirickService, ModelMapper mapper) {
        this.timelineRepository = timelineRepository;
        this.userService = userService;
        this.chirickService = chirickService;
        this.mapper = mapper;
    }

    private Chirick getChirick(String chirickId) {
        Chirick serviceModel = this.chirickService.getById(chirickId);
        if (serviceModel == null) return null;
        return serviceModel;
    }

    private User getUserWithHandle(String handle) throws UserNotFoundException {
        User userModel = this.userService.getByHandle(handle);
        if (userModel == null) throw new UserNotFoundException("No user with " + handle + " was found");
        return userModel;
    }

    @Override
    public List<TimelinePostViewModel> getTimelineForUser(String userHandle, Pageable pageable) throws UserNotFoundException {
        User user = this.getUserWithHandle(userHandle);

        Set<TimelinePost> posts = user.getTimeline().getPosts();
        List<TimelinePost> timelinePosts = new ArrayList<>(posts);
        timelinePosts.sort((p1, p2) -> p2.getDate().compareTo(p1.getDate()));

        int startingIndex = Math.max(pageable.getPageNumber(), 0);
        int endIndex = Math.min(pageable.getPageSize(), posts.size());

        List<TimelinePostViewModel> viewModels = new ArrayList<>(pageable.getPageSize());
        for (int i = startingIndex; i < endIndex; i++) {
            TimelinePost timelinePost = timelinePosts.get(i);
            TimelinePostViewModel viewModel = this.mapper.map(timelinePost, TimelinePostViewModel.class);

            ChirickViewModel chirickModel = viewModel.getChirick();
            Chirick chirick = timelinePost.getChirick();

            chirickModel.setRechiricksSize(chirick.getRechiricks().size());
            chirickModel.setRechiricked(chirick.getRechiricks().contains(user));

            chirickModel.setLikesSize(chirick.getLikes().size());
            chirickModel.setLiked(chirick.getLikes().contains(user));

            chirickModel.setCommentsSize(chirick.getComments().size());
            chirickModel.setCommented(chirick.getComments().stream()
                    .anyMatch(c -> c.getUser() == user));

            if (chirick.getParent() != null) {
                Chirick parent = chirick.getParent();
                String parentUrl = "/@" + parent.getUser().getHandle() + "/" + parent.getId();
                chirickModel.setParentUrl(parentUrl);
            }

            viewModels.add(viewModel);
        }

        return viewModels;
    }

    @Async
    @Override
    @Transactional
    public Future updateTimeline(String userHandle, String chirickId, TimelinePostType type, boolean isAdding) throws UserNotFoundException {
        Chirick chirick = this.getChirick(chirickId);
        if (chirick == null) return null;

        User user = this.getUserWithHandle(userHandle);

        Set<Timeline> timelines =
                user.getFollowers()
                        .stream()
                        .map(User::getTimeline)
                        .collect(Collectors.toSet());

        for (Timeline timeline : timelines) {
            if (isAdding) {
                TimelinePost timelinePost = new TimelinePost();
                timelinePost.setChirick(chirick);
                timelinePost.setUser(user);
                timelinePost.setPostType(type);

                timeline.getPosts().add(timelinePost);
            } else {
                TimelinePost existingPost = timeline.getPosts()
                        .stream()
                        .filter(p -> p.getChirick() == chirick
                                && p.getUser() == user
                                && p.getPostType() == type)
                        .findFirst()
                        .orElse(null);

                if (existingPost != null) {
                    timeline.getPosts().remove(existingPost);
                }
            }
        }

        this.timelineRepository.saveAll(timelines);

        return new AsyncResult("");
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

        for (TimelinePost post : posts) {
            if (post.getUser() == user) {
                postsToDelete.add(post);
            }
        }

        requesterTimeline.getPosts().removeAll(postsToDelete);

        this.timelineRepository.save(requesterTimeline);

        return new AsyncResult("");
    }
}