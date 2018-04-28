package com.chiricker.areas.chiricks.repositories;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.users.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TimelinePostRepository extends PagingAndSortingRepository<TimelinePost, String> {

    TimelinePost findByChirickIdAndFromIdAndToIdAndTimelineIdAndPostType(String chirickId, String fromId, String toId, String timelineId, TimelinePostType type);

    TimelinePost findByChirickAndFromAndToIdAndTimelineIdAndPostType(Chirick chirick, User from, String toId, String timelineId, TimelinePostType type);

    Page<TimelinePost> findAllByTimelineIdAndFromIsEnabledTrueOrderByDateDesc(String timeline, Pageable pageable);

    List<TimelinePost> deleteAllByFromHandleAndToHandle(String from, String to);
}