package com.chiricker.areas.home.controllers;

import com.chiricker.areas.home.models.view.IndexViewModel;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.view.ProfileViewModel;
import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.controllers.BaseController;
import com.chiricker.areas.home.utils.greeting.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class HomeController extends BaseController {

    private final UserService userService;
    private final Greeting greeting;

    @Autowired
    public HomeController(UserService userService, Greeting greeting) {
        this.userService = userService;
        this.greeting = greeting;
    }

    @GetMapping("/")
    public ModelAndView index(Principal principal) throws UserNotFoundException {
        if (principal == null) return this.view("home/index");
        ProfileViewModel profileViewModel = this.userService.getProfileByHandle(principal.getName(), principal.getName());
        return this.view("home/index", "indexModel", new IndexViewModel(profileViewModel, this.greeting.getGreetingStatus()));
    }
}