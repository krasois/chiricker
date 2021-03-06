package com.chiricker.areas.users.controllers;

import com.chiricker.areas.chiricks.services.timeline.TimelineService;
import com.chiricker.areas.logger.annotations.Logger;
import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.binding.FollowBindingModel;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.view.FollowResultViewModel;
import com.chiricker.areas.users.models.view.FollowerViewModel;
import com.chiricker.areas.users.models.view.UserCardViewModel;
import com.chiricker.areas.users.models.view.UserNavbarViewModel;
import com.chiricker.areas.users.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserRestController {

    private final UserService userService;
    private final TimelineService timelineService;

    @Autowired
    public UserRestController(UserService userService, TimelineService timelineService) {
        this.userService = userService;
        this.timelineService = timelineService;
    }

    @GetMapping("/card")
    public ResponseEntity<UserCardViewModel> userCard(Principal principal) throws UserNotFoundException {
        UserCardViewModel model = this.userService.getUserCard(principal.getName());
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @GetMapping("/navbar")
    public ResponseEntity<UserNavbarViewModel> navbar(Principal principal) throws UserNotFoundException {
        UserNavbarViewModel model = this.userService.getNavbarInfo(principal.getName());
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/follow")
    @Logger(entity = User.class, operation = Operation.FOLLOW)
    public FollowResultViewModel follow(FollowBindingModel model, Principal principal) throws UserNotFoundException {
        FollowResultViewModel followResult = this.userService.follow(model, principal.getName());
        if (followResult.isUnfollowed()) this.timelineService.unfollow(model.getHandle(), principal.getName());
        return followResult;
    }

    @GetMapping("/@{handle}/followers")
    public ResponseEntity<List<FollowerViewModel>> followers(@PathVariable("handle") String handle, Principal principal, @PageableDefault(size = 20) Pageable pageable) throws UserNotFoundException {
        List<FollowerViewModel> models = this.userService.getFollowersForUser(handle, principal.getName(), pageable);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @GetMapping("/@{handle}/following")
    public ResponseEntity<List<FollowerViewModel>> following(@PathVariable("handle") String handle, Principal principal, @PageableDefault(size = 20) Pageable pageable) throws UserNotFoundException {
        List<FollowerViewModel> models = this.userService.getFollowingForUser(handle, principal.getName(), pageable);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }
}