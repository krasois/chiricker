package com.chiricker.areas.users.controllers;

import com.chiricker.areas.logger.annotations.Logger;
import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.view.PeerSearchResultViewModel;
import com.chiricker.areas.users.models.view.ProfileViewModel;
import com.chiricker.util.uploader.FileUploader;
import com.chiricker.controllers.BaseController;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.exceptions.UserRoleNotFoundException;
import com.chiricker.areas.users.models.binding.UserLoginBindingModel;
import com.chiricker.areas.users.models.binding.UserRegisterBindingModel;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
import com.chiricker.areas.users.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController extends BaseController {

    private final UserService userService;
    private final FileUploader fileUploader;

    @Autowired
    public UserController(UserService userService, FileUploader fileUploader) {
        this.userService = userService;
        this.fileUploader = fileUploader;
    }

    @GetMapping("/login")
    @PreAuthorize("!isAuthenticated()")
    public ModelAndView login(@ModelAttribute("user") UserLoginBindingModel user, @RequestParam(required = false) String error) {
        if (error != null) return this.view("users/login", "error", "Handle and password do not match");
        return this.view("users/login");
    }

    @GetMapping("/register")
    @PreAuthorize("!isAuthenticated()")
    public ModelAndView register(@ModelAttribute("user") UserRegisterBindingModel user) {
        return this.view("users/register");
    }

    @PostMapping("/register")
    @PreAuthorize("!isAuthenticated()")
    @Logger(entity = User.class, operation = Operation.REGISTER)
    public ModelAndView register(@Valid @ModelAttribute("user") UserRegisterBindingModel user, BindingResult result) throws UserRoleNotFoundException {
        if (result.hasErrors()) return this.view("users/register");
        this.userService.register(user);
        return this.redirect("/login");
    }

    @GetMapping("/settings")
    public ModelAndView settings(@ModelAttribute("user") UserEditBindingModel user, Principal principal) throws UserNotFoundException {
        UserEditBindingModel userDetails = this.userService.getUserSettings(principal.getName());
        return this.view("users/settings", "user", userDetails);
    }

    @PostMapping(value = "/settings")
    @Logger(entity = User.class, operation = Operation.SETTINGS_CHANGE)
    public ModelAndView settings(@Valid @ModelAttribute("user") UserEditBindingModel user, BindingResult result, Principal principal) throws UserNotFoundException {
        if (result.hasErrors()) return this.view("users/settings");
        this.userService.edit(user, principal.getName());
        this.fileUploader.uploadFile(user.getHandle(), user.getProfilePicture());
        return this.redirect("/@" + principal.getName());
    }

    @GetMapping("/@{handle}")
    public ModelAndView userProfile(@PathVariable("handle") String handle, Principal principal) throws UserNotFoundException {
        return this.view("users/profile/activity", "profile", this.userService.getProfileByHandle(handle, principal.getName()));
    }

    @GetMapping("/profile")
    public ModelAndView profile(Principal principal) {
        return this.redirect("/@" + principal.getName());
    }

    @GetMapping("/@{handle}/followers")
    public ModelAndView followers(@PathVariable("handle") String handle, Principal principal) throws UserNotFoundException {
        ProfileViewModel profileModel = this.userService.getProfileByHandle(handle, principal.getName());
        return this.view("/users/profile/followers", "profile", profileModel);
    }

    @GetMapping("/@{handle}/following")
    public ModelAndView following(@PathVariable("handle") String handle, Principal principal) throws UserNotFoundException {
        ProfileViewModel profileModel = this.userService.getProfileByHandle(handle, principal.getName());
        return this.view("/users/profile/following", "profile", profileModel);
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam("query") String query, Principal principal, @PageableDefault(size = 20) Pageable pageable) throws UserNotFoundException {
        PeerSearchResultViewModel result = this.userService.getPeers(query, principal.getName(), pageable);
        return this.view("users/peers", "result", result);
    }
}