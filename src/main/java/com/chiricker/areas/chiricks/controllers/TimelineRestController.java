package com.chiricker.areas.chiricks.controllers;

import com.chiricker.areas.chiricks.models.view.TimelinePostViewModel;
import com.chiricker.areas.chiricks.services.timeline.TimelineService;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class TimelineRestController {

    private final TimelineService timelineService;

    @Autowired
    public TimelineRestController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<TimelinePostViewModel>> getUserNewsfeed(@PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        List<TimelinePostViewModel> models = this.timelineService.getTimelineForUser(principal.getName(), pageable);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }
}