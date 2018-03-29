package com.chiricker.users.controllers;

import com.chiricker.users.models.view.UserCardViewModel;
import com.chiricker.users.models.view.UserNavbarViewModel;
import com.chiricker.users.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/card")
    public UserCardViewModel userCard(Principal principal) {
        return this.userService.getUserCard(principal.getName());
    }

    @GetMapping("/navbar")
    public UserNavbarViewModel navbar(Principal principal) {
        return this.userService.getNavbarInfo(principal.getName());
    }
}