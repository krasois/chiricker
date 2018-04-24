package com.chiricker.areas.chiricks.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.repositories.TimelinePostRepository;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.util.mapper.CustomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TimelinePostServiceImpl implements TimelinePostService {

    private final TimelinePostRepository timelinePostRepository;
    private final CustomMapper customMapper;

    @Autowired
    public TimelinePostServiceImpl(TimelinePostRepository timelinePostRepository, CustomMapper customMapper) {
        this.timelinePostRepository = timelinePostRepository;
        this.customMapper = customMapper;
    }

    @Override
    public Page<TimelinePost> getPostsFromTimeline(Timeline timeline, Pageable pageable) {
        return this.timelinePostRepository.findAllByTimelineOrderByDateDesc(timeline, pageable);
    }

    @Override
    public String getPostIdByFields(Timeline timeline, Chirick chirick, User from, TimelinePostType type) {
        TimelinePost post = this.timelinePostRepository.findByChirickAndFromAndToAndTimelineAndPostType(chirick, from, timeline.getUser(), timeline, type);
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
    public List<TimelinePost> deletePostsFromUserToUser(String userHandle, String requesterHandle) {
        return this.timelinePostRepository.deleteAllByFromHandleAndToHandle(userHandle, requesterHandle);
    }

    @Override
    public TimelinePostServiceModel createPost(Timeline timeline, Chirick chirick, User from, TimelinePostType type) {
        TimelinePost post = this.timelinePostRepository.findByChirickIdAndFromIdAndToIdAndTimelineIdAndPostType(chirick.getId(), from.getId(), timeline.getUser().getId(), timeline.getId(), type);
        if (post == null) {
            post = new TimelinePost();
            post.setChirick(chirick);
            post.setFrom(from);
            post.setTo(timeline.getUser());
            post.setTimeline(timeline);
            post.setPostType(type);
            post = this.timelinePostRepository.save(post);
        }

        return this.customMapper.postToServiceModel(post);
    }
}