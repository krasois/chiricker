package com.chiricker.areas.chiricks.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelineUserServiceModel;
import com.chiricker.areas.chiricks.models.view.TimelinePostViewModel;
import com.chiricker.areas.users.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TimelinePostService {

    TimelinePostServiceModel createPost(TimelineUserServiceModel timeline, Chirick chirick, User from, TimelinePostType type);

    Page<TimelinePostViewModel> getPostsFromTimeline(String timelineId, String userId, Pageable pageable);

    String getPostIdByFields(TimelineUserServiceModel timeline, Chirick chirick, User user, TimelinePostType type);

    void deletePosts(List<TimelinePost> posts);

    List<TimelinePostServiceModel> deletePostsFromUserToUser(String userHandle, String requesterHandle);
}