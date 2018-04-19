package com.chiricker.areas.chiricks.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface TimelinePostService {

    Page<TimelinePostServiceModel> getPostsInCollection(Set<TimelinePost> posts, Pageable pageable);
}