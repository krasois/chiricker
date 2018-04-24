package com.chiricker.areas.chiricks.services.timeline;

import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.view.TimelinePostViewModel;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.Future;

public interface TimelineService {

    List<TimelinePostViewModel> getTimelineForUser(String userHandle, Pageable pageable) throws UserNotFoundException;

    Future updateTimeline(String userHandle, String chirickId, TimelinePostType type, boolean isAdding) throws UserNotFoundException;

    Future unfollow(String userHandle, String requesterHandle) throws UserNotFoundException;
}