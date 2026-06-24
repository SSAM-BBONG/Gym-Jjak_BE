package com.ssambbong.gymjjak.notification.domain.repository;

import com.ssambbong.gymjjak.notification.domain.model.Notification;

import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long notificationId);

}
