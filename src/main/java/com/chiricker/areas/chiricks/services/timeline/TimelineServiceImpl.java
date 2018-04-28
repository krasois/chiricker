package com.chiricker.areas.chiricks.services.timeline;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelineUserServiceModel;
import com.chiricker.areas.chiricks.models.service.UserServiceModelTP;
import com.chiricker.areas.chiricks.models.view.ChirickViewModel;
import com.chiricker.areas.chiricks.models.view.TimelinePostViewModel;
import com.chiricker.areas.chiricks.repositories.TimelineRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.areas.chiricks.services.timelinePost.TimelinePostService;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.UserServiceModel;
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

    private Timeline removeFromTimeline(TimelineUserServiceModel timeline, Chirick chirick, User user, TimelinePostType type) {
        String postId = this.timelinePostService.getPostIdByFields(timeline, chirick, user, type);
        if (postId == null) return null;
        Timeline timelineRaw = this.timelineRepository.findById(timeline.getId()).orElse(null);
        if (timelineRaw == null) return null;

        List<TimelinePost> toRemove = timelineRaw.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .collect(Collectors.toList());

        timelineRaw.getPosts().removeAll(toRemove);
        this.timelinePostService.deletePosts(toRemove);
        return timelineRaw;
    }

    @Override
    public List<TimelinePostViewModel> getTimelineForUser(String userHandle, Pageable pageable) {
        String userId = this.userService.getIdForHandle(userHandle);
        Timeline userTimeline = this.timelineRepository.findByUserId(userId);
        if (userTimeline == null) return new ArrayList<>();

        if (userTimeline.getPosts().size() < 1) return new ArrayList<>();
        Page<TimelinePostViewModel> posts = this.timelinePostService.getPostsFromTimeline(userTimeline.getId(), userId, pageable);

        return posts.getContent();
    }

    @Async
    @Override
    @Transactional
    public Future updateTimeline(String userHandle, String chirickId, TimelinePostType type, boolean isAdding) {
        Chirick chirick = this.getChirick(chirickId);
        if (chirick == null) return null;

        String userId = this.userService.getIdForHandle(userHandle);
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);

        Set<TimelineUserServiceModel> timelines = this.userService.getUserFollowerTimelineIds(userId);
        Set<Timeline> rawTimelines = new HashSet<>();

        for (TimelineUserServiceModel timeline : timelines) {
            if (isAdding) {
                this.timelinePostService.createPost(timeline, chirick, user, type);
            } else {
                Timeline timelineRaw = this.removeFromTimeline(timeline, chirick, user, type);
                rawTimelines.add(timelineRaw);
            }
        }

        return new AsyncResult<>(isAdding ? timelines : rawTimelines);
    }

    @Async
    @Override
    @Transactional
    public Future unfollow(String userHandle, String requesterHandle) {
        List<TimelinePostServiceModel> deletedTimelinePosts = this.timelinePostService.deletePostsFromUserToUser(userHandle, requesterHandle);
        if (deletedTimelinePosts == null) return null;

        return new AsyncResult<>(deletedTimelinePosts);
    }
}