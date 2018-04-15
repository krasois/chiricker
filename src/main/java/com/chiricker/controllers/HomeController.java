package com.chiricker.controllers;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class HomeController extends BaseController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView index(Principal principal) throws UserNotFoundException {
        if (principal == null) return this.view("home/index");
        return this.view("home/index", "profile", this.userService.getProfileByHandle(principal.getName(), principal.getName()));
    }
}