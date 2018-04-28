package com.chiricker.areas.chiricks.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelineUserServiceModel;
import com.chiricker.areas.chiricks.models.view.ChirickViewModel;
import com.chiricker.areas.chiricks.models.view.TimelinePostViewModel;
import com.chiricker.areas.chiricks.repositories.TimelinePostRepository;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.util.linker.UserLinker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimelinePostServiceImpl implements TimelinePostService {

    private final TimelinePostRepository timelinePostRepository;
    private final ModelMapper mapper;

    @Autowired
    public TimelinePostServiceImpl(TimelinePostRepository timelinePostRepository, ModelMapper mapper) {
        this.timelinePostRepository = timelinePostRepository;
        this.mapper = mapper;
    }

    private TimelinePostViewModel mapToViewModel(TimelinePost post, String userId) {
        TimelinePostViewModel viewModel = new TimelinePostViewModel();
        viewModel.setFromHandle(post.getFrom().getHandle());
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
                .anyMatch(u -> u.getId().equals(userId)));

        chirickModel.setLikesSize(chirick.getLikes().size());
        chirickModel.setLiked(chirick.getLikes().stream()
                .anyMatch(u -> u.getId().equals(userId)));

        chirickModel.setCommentsSize(chirick.getComments().size());
        chirickModel.setCommented(chirick.getComments().stream()
                .anyMatch(u -> u.getUser().getId().equals(userId)));

        if (chirick.getParent() != null) {
            Chirick parent = chirick.getParent();
            String parentUrl = "/@" + parent.getUser().getHandle() + "/" + parent.getId();
            chirickModel.setParentUrl(parentUrl);
        }

        viewModel.setChirick(chirickModel);
        return viewModel;
    }

    @Override
    public Page<TimelinePostViewModel> getPostsFromTimeline(String timelineId, String userId, Pageable pageable) {
        Page<TimelinePost> posts = this.timelinePostRepository.findAllByTimelineIdAndFromIsEnabledTrueOrderByDateDesc(timelineId, pageable);
        return posts.map(p -> this.mapToViewModel(p, userId));
    }

    @Override
    public String getPostIdByFields(TimelineUserServiceModel timeline, Chirick chirick, User from, TimelinePostType type) {
        TimelinePost post = this.timelinePostRepository.findByChirickAndFromAndToIdAndTimelineIdAndPostType(chirick, from, timeline.getUserId(), timeline.getId(), type);
        return post == null ? null : post.getId();
    }

    @Override
    public void deletePosts(List<TimelinePost> collect) {
        for (TimelinePost timelinePost : collect) {
            this.timelinePostRepository.deleteById(timelinePost.getId());
        }
    }

    @Override
    @Transactional
    public List<TimelinePostServiceModel> deletePostsFromUserToUser(String userHandle, String requesterHandle) {
        return this.timelinePostRepository.deleteAllByFromHandleAndToHandle(userHandle, requesterHandle)
                .stream()
                .map(p -> mapper.map(p, TimelinePostServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public TimelinePostServiceModel createPost(TimelineUserServiceModel timeline, Chirick chirick, User from, TimelinePostType type) {
        TimelinePost post = this.timelinePostRepository.findByChirickIdAndFromIdAndToIdAndTimelineIdAndPostType(chirick.getId(), from.getId(), timeline.getUserId(), timeline.getId(), type);
        if (post == null) {
            post = new TimelinePost();
            post.setChirick(chirick);
            post.setFrom(from);
            post.setTo(new User() {{ setId(timeline.getUserId()); }});
            post.setTimeline(new Timeline() {{ setId(timeline.getId()); }});
            post.setPostType(type);
            post = this.timelinePostRepository.save(post);
        }

        return this.mapper.map(post, TimelinePostServiceModel.class);
    }
}