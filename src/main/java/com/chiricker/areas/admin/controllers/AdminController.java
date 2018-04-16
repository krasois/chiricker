package com.chiricker.areas.admin.controllers;

import com.chiricker.areas.admin.models.EditUserBindingModel;
import com.chiricker.areas.admin.models.view.UserPanelViewModel;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.areas.users.utils.FileUploader;
import com.chiricker.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController extends BaseController {

    private final UserService userService;
    private final FileUploader fileUploader;

    @Autowired
    public AdminController(UserService userService, FileUploader fileUploader) {
        this.userService = userService;
        this.fileUploader = fileUploader;
    }

    @GetMapping("")
    public ModelAndView panel() {
        return this.view("admin/panel");
    }

    @GetMapping("/users")
    public ModelAndView users(@PageableDefault(size = 20) Pageable pageable) {
        Page<UserPanelViewModel> users = this.userService.getEnabledUsersForAdmin(pageable);
        return this.view("admin/users", "users", users);
    }

    @GetMapping("/users/disabled")
    public ModelAndView disabled(@PageableDefault(size = 20) Pageable pageable) {
        Page<UserPanelViewModel> users = this.userService.getDisabledUsersForAdmin(pageable);
        return this.view("admin/disabled", "users", users);
    }

    @GetMapping("/users/edit/{id}")
    public ModelAndView editUser(@PathVariable("id") String id, @ModelAttribute("user") EditUserBindingModel user) throws UserNotFoundException {
        EditUserBindingModel userEditModel = this.userService.getUserSettingsAdmin(id);
        return this.view("admin/edit", "user", userEditModel);
    }

    @PostMapping("/users/edit/{id}")
    public ModelAndView editUser(@PathVariable("id") String id, @Valid @ModelAttribute("user") EditUserBindingModel user, BindingResult result) throws UserNotFoundException {
        if (result.hasErrors()) return this.view("admin/edit");
        this.userService.editAdmin(id, user);
        this.fileUploader.uploadFile(user.getHandle(), user.getProfilePicture());
        return this.redirect("/admin");
    }

    @GetMapping("/users/delete/{id}")
    public ModelAndView deleteUser(@PathVariable("id") String id) throws UserNotFoundException {
        EditUserBindingModel userModel = this.userService.getUserSettingsAdmin(id);
        return this.view("admin/delete", "user", userModel);
    }

    @PostMapping("/users/delete/{id}")
    public ModelAndView deactivateUser(@PathVariable("id") String id) throws UserNotFoundException {
        this.userService.disableUser(id);
        return this.redirect("/admin");
    }

    @GetMapping("/users/enable/{id}")
    public ModelAndView disabledUser(@PathVariable("id") String id) throws UserNotFoundException {
        EditUserBindingModel userModel = this.userService.getUserSettingsAdmin(id);
        return this.view("admin/enable", "user", userModel);
    }

    @PostMapping("/users/enable/{id}")
    public ModelAndView enableUser(@PathVariable("id") String id) throws UserNotFoundException {
        this.userService.enableUser(id);
        return this.redirect("/admin");
    }
}