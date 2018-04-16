package com.chiricker.areas.admin.controllers;

import com.chiricker.areas.admin.models.binding.EditUserBindingModel;
import com.chiricker.areas.admin.models.view.LogViewModel;
import com.chiricker.areas.admin.models.view.UserPanelViewModel;
import com.chiricker.areas.logger.annotations.Logger;
import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.areas.logger.services.log.LogService;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.areas.users.utils.FileUploader;
import com.chiricker.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final LogService logService;
    private final FileUploader fileUploader;

    @Autowired
    public AdminController(UserService userService, LogService logService, FileUploader fileUploader) {
        this.userService = userService;
        this.logService = logService;
        this.fileUploader = fileUploader;
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

    @GetMapping("/logs")
    public ModelAndView logs(@PageableDefault(size = 15, direction = Sort.Direction.DESC, sort = "date") Pageable pageable) {
        return this.view("admin/logs", "logs", this.logService.getLogs(pageable));
    }

    @GetMapping("/users/edit/{id}")
    public ModelAndView editUser(@PathVariable("id") String id, @ModelAttribute("user") EditUserBindingModel user) throws UserNotFoundException {
        EditUserBindingModel userEditModel = this.userService.getUserSettingsAdmin(id);
        return this.view("admin/edit", "user", userEditModel);
    }

    @PostMapping("/users/edit/{id}")
    @Logger(entity = User.class, operation = Operation.SETTINGS_CHANGE_ADMIN)
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
    @Logger(entity = User.class, operation = Operation.DISABLE_USER)
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
    @Logger(entity = User.class, operation = Operation.ENABLE_USER)
    public ModelAndView enableUser(@PathVariable("id") String id) throws UserNotFoundException {
        this.userService.enableUser(id);
        return this.redirect("/admin");
    }
}