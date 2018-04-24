package com.chiricker.areas.chiricks.services.timeline;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.view.ChirickViewModel;
import com.chiricker.areas.chiricks.models.view.TimelinePostViewModel;
import com.chiricker.areas.chiricks.repositories.TimelineRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.areas.chiricks.services.timelinePost.TimelinePostService;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.util.linker.UserLinker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class TimelineServiceImpl implements TimelineService {

    private final TimelineRepository timelineRepository;
    private final UserService userService;
    private final ChirickService chirickService;
    private final TimelinePostService timelinePostService;
    private final ModelMapper mapper;

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

    private User getUserWithHandle(String handle) {
        return this.userService.getByHandle(handle);
    }

    private TimelinePostViewModel mapPostToViewModel(TimelinePost post, User user) {
        TimelinePostViewModel viewModel = new TimelinePostViewModel();
        viewModel.setPosterHandle(post.getFrom().getHandle());
        viewModel.setPostTypeValue(post.getPostType().getValue());

        User originalPoster = post.getChirick().getUser();
        ChirickViewModel chirickModel = new ChirickViewModel();
        chirickModel.setUserName(originalPoster.getName());
        chirickModel.setUserHandle(originalPoster.getHandle());
        chirickModel.setUserProfilePicUrl(originalPoster.getProfile().getProfilePicUrl());

        chirickModel.setId(post.getChirick().getId());
        chirickModel.setChirick(UserLinker.linkUsers(
                post.getChirick().getChirick()));

        Chirick chirick = post.getChirick();

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

        viewModel.setChirick(chirickModel);
        return viewModel;
    }

    private void addToTimeline(Timeline timeline, Chirick chirick, User user, TimelinePostType type) {
        TimelinePostServiceModel postModel = this.timelinePostService.createPost(timeline, chirick, user, type);
        TimelinePost timelinePost = new TimelinePost();
        timelinePost.setId(postModel.getId());
        timelinePost.setDate(postModel.getDate());
        timelinePost.setPostType(postModel.getPostType());
        timelinePost.setTimeline(this.mapper.map(postModel.getTimeline(), Timeline.class));
        timelinePost.setChirick(this.mapper.map(postModel.getChirick(), Chirick.class));
        timelinePost.setTo(this.mapper.map(postModel.getTo(), User.class));
        timelinePost.setFrom(this.mapper.map(postModel.getFrom(), User.class));
        timeline.getPosts().add(timelinePost);
    }

    private void removeFromTimeline(Timeline timeline, Chirick chirick, User user, TimelinePostType type) {
        String postId = this.timelinePostService.getPostIdByFields(timeline, chirick, user, type);
        if (postId == null) return;
        List<TimelinePost> toRemove = timeline.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .collect(Collectors.toList());

        timeline.getPosts().removeAll(toRemove);
        this.timelinePostService.deletePosts(toRemove);
    }

    @Override
    public List<TimelinePostViewModel> getTimelineForUser(String userHandle, Pageable pageable) {
        User user = this.getUserWithHandle(userHandle);

        if (user.getTimeline().getPosts().size() < 1) return new ArrayList<>();
        Timeline timeline = this.mapper.map(user.getTimeline(), Timeline.class);
        Page<TimelinePost> posts = this.timelinePostService.getPostsFromTimeline(timeline, pageable);

        Page<TimelinePostViewModel> map = posts.map(p -> mapPostToViewModel(p, user));
        return map.getContent();
    }

    @Async
    @Override
    @Transactional
    public Future updateTimeline(String userHandle, String chirickId, TimelinePostType type, boolean isAdding) {
        Chirick chirick = this.getChirick(chirickId);
        if (chirick == null) return null;

        User user = this.getUserWithHandle(userHandle);
        if (user == null) return null;

        Set<Timeline> timelines = user.getFollowers().stream()
                .map(User::getTimeline)
                .collect(Collectors.toSet());

        for (Timeline timeline : timelines) {
            if (isAdding) {
                this.addToTimeline(timeline, chirick, user, type);
                this.timelineRepository.saveAndFlush(timeline);
            } else {
                this.removeFromTimeline(timeline, chirick, user, type);
            }
        }

        return new AsyncResult<>(timelines);
    }

    @Async
    @Override
    @Transactional
    public Future unfollow(String userHandle, String requesterHandle) {
        List<TimelinePost> deletedTimelinePosts = this.timelinePostService.deletePostsFromUserToUser(userHandle, requesterHandle);
        if (deletedTimelinePosts == null) return null;

        return new AsyncResult<>(deletedTimelinePosts);
    }
}