package com.chiricker.areas.users.schedules;

import com.chiricker.areas.users.models.entities.Notification;
import com.chiricker.areas.users.services.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledNotificationCleaner {

    private final NotificationService notificationService;

    @Autowired
    public ScheduledNotificationCleaner(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanNotifications() {
        List<Notification> deletedNotifications = this.notificationService.cleanNotifications();
        System.out.printf("Deleted %s notifications.", deletedNotifications.size());
    }
}