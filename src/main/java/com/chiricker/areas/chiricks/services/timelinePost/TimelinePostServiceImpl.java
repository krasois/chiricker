package com.chiricker.areas.chiricks.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelineUserServiceModel;
import com.chiricker.areas.chiricks.repositories.TimelinePostRepository;
import com.chiricker.areas.users.models.entities.User;
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

    private TimelinePostServiceModel mapToModel(TimelinePost post) {
        TimelinePostServiceModel mappedPost = this.mapper.map(post, TimelinePostServiceModel.class);
        mappedPost.setChirick(this.mapper.map(post.getChirick(), ChirickServiceModel.class));
        return mappedPost;
    }

    @Override
    public Page<TimelinePostServiceModel> getPostsFromTimeline(String timelineId, Pageable pageable) {
        Page<TimelinePost> posts = this.timelinePostRepository.findAllByTimelineIdOrderByDateDesc(timelineId, pageable);
        return posts.map(this::mapToModel);
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