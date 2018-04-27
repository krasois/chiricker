package com.chiricker.areas.users.services.notification;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.users.models.entities.Notification;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.SimpleUserServiceModel;
import com.chiricker.areas.users.models.view.NotificationViewModel;
import com.chiricker.areas.users.repositories.NotificationRepository;
import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.util.linker.UserLinker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final String USER_PATTERN = "(@\\w+)";
    private static final Pattern HANDLE_PATTERN = Pattern.compile(USER_PATTERN);
    private static final boolean isChecked = true;

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserService userService, ModelMapper mapper) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    private Set<String> extractHandles(String content) {
        Set<String> handles = new HashSet<>();
        Matcher matcher = HANDLE_PATTERN.matcher(content);
        while (matcher.find()) {
            String handle = matcher.group(0).trim().substring(1);
            handles.add(handle);
        }

        return handles;
    }

    private NotificationViewModel mapNotification(Notification notification) {
        NotificationViewModel model = new NotificationViewModel();
        model.setId(notification.getId());

        String chirickContent = UserLinker.linkUsers(notification.getChirick().getChirick());
        model.setChirickContent(chirickContent);
        model.setChirickId(notification.getChirick().getId());

        model.setUserHandle(notification.getChirick().getUser().getHandle());
        model.setUserName(notification.getChirick().getUser().getName());

        return model;
    }

    private User getUserWithHandle(String handle) {
        SimpleUserServiceModel userModel = this.userService.getByHandleSimple(handle);
        if (userModel == null) return null;
        return this.mapper.map(userModel, User.class);
    }

    @Override
    public List<NotificationViewModel> getNotifications(String handle) {
        List<Notification> notifications = this.notificationRepository.findAllByCheckedFalseAndNotifiedHandleOrderByDateDesc(handle);
        return notifications.stream()
                .map(this::mapNotification)
                .collect(Collectors.toList());
    }

    @Async
    @Override
    @Transactional
    public Future notifyUsers(Chirick chirick) {
        Set<String> handles = this.extractHandles(chirick.getChirick());
        if (handles.size() < 1) return null;

        Set<Notification> notifications = new HashSet<>();
        for (String handle : handles) {
            User user = this.getUserWithHandle(handle);
            if (user == null || user.getId().equals(chirick.getUser().getId())) continue;

            Notification notification = new Notification();
            notification.setChirick(chirick);
            notification.setNotified(user);
            notification = this.notificationRepository.save(notification);

            notifications.add(notification);
        }

        return new AsyncResult<>(notifications);
    }

    @Async
    @Override
    @Transactional
    public Future updateChecked(List<NotificationViewModel> notifications) {
        Set<String> ids = notifications.stream()
                .map(NotificationViewModel::getId)
                .collect(Collectors.toSet());

        List<Notification> updatedNotifications = new ArrayList<>();
        for (String id : ids) {
            Notification notification = this.notificationRepository.findById(id).orElse(null);
            if (notification == null) continue;
            notification.setChecked(isChecked);
            notification = this.notificationRepository.saveAndFlush(notification);
            updatedNotifications.add(notification);
        }

        return new AsyncResult<>(updatedNotifications);
    }

    @Override
    @Transactional
    public List<Notification> cleanNotifications() {
        return this.notificationRepository.deleteAllByCheckedTrue();
    }
}