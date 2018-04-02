package com.chiricker.users.controllers;

import com.chiricker.controllers.BaseController;
import com.chiricker.users.models.binding.UserLoginBindingModel;
import com.chiricker.users.models.binding.UserRegisterBindingModel;
import com.chiricker.users.models.binding.UserEditBindingModel;
import com.chiricker.users.services.role.RoleService;
import com.chiricker.users.services.role.RoleServiceImpl;
import com.chiricker.users.services.user.UserService;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController extends BaseController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView register(@ModelAttribute("user") UserRegisterBindingModel user) {
        return this.view("users/register");
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("user") UserRegisterBindingModel user, BindingResult result) {
        if (result.hasErrors()) { return this.view("users/register"); }
        this.userService.register(user);
        return this.redirect("/login");
    }

    @GetMapping("/login")
    public ModelAndView login(@ModelAttribute("user") UserLoginBindingModel user, @RequestParam(required = false) String error) {
        if (error != null) { return this.view("users/login", "error", "Handle and password do not match"); }
        return this.view("users/login");
    }

    @GetMapping("/settings")
    public ModelAndView settings(@ModelAttribute("user") UserEditBindingModel user, Principal principal) {
        UserEditBindingModel userDetails = this.userService.getUserSettings(principal.getName());
        return this.view("users/settings", "user", userDetails);
    }

    @PostMapping(value = "/settings")
    public ModelAndView settings(@Valid @ModelAttribute("user") UserEditBindingModel user, BindingResult result, Principal principal) {
        if (result.hasErrors()) { return this.view("users/settings"); }
        this.userService.edit(user, principal.getName());
        return this.redirect("/" + principal.getName());
    }
}