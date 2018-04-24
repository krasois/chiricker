package com.chiricker.areas.users.repositories;

import com.chiricker.areas.users.models.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByCheckedFalseAndNotifiedHandleOrderByDateDesc(String handle);

    List<Notification> deleteAllByCheckedTrue();
}