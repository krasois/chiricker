package com.chiricker.areas.users.controllers;

import com.chiricker.areas.users.models.view.NotificationViewModel;
import com.chiricker.areas.users.services.notification.NotificationService;
import com.chiricker.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ModelAndView getNotifications(Principal principal) {
        List<NotificationViewModel> notifications = this.notificationService.getNotifications(principal.getName());
        this.notificationService.updateChecked(notifications);
        return this.view("users/notifications", "notifications", notifications);
    }
}