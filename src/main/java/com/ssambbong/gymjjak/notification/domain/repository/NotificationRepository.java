package com.ssambbong.gymjjak.notification.domain.repository;

import com.ssambbong.gymjjak.notification.domain.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> saveAll(List<Notification> notifications);

    Optional<Notification> findById(Long notificationId);

    List<Notification> findAllById(List<Long> notificationIds);

}
