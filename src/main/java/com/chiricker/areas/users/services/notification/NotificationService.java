package com.chiricker.areas.users.services.notification;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.users.models.entities.Notification;
import com.chiricker.areas.users.models.view.NotificationViewModel;

import java.util.List;
import java.util.concurrent.Future;

public interface NotificationService {

    List<NotificationViewModel> getNotifications(String handle);

    Future notifyUsers(Chirick chirick);

    Future updateChecked(List<NotificationViewModel> notifications);

    List<Notification> cleanNotifications();
}
