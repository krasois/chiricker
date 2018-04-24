package com.chiricker.areas.chiricks.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.users.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TimelinePostService {

    TimelinePostServiceModel createPost(Timeline timeline, Chirick chirick, User user, TimelinePostType type);

    Page<TimelinePost> getPostsFromTimeline(Timeline timeline, Pageable pageable);

    String getPostIdByFields(Timeline timeline, Chirick chirick, User user, TimelinePostType type);

    void deletePosts(List<TimelinePost> posts);

    List<TimelinePost> deletePostsFromUserToUser(String userHandle, String requesterHandle);
}