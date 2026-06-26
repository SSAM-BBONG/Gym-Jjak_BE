package com.ssambbong.gymjjak.notification.domain.repository;

import com.ssambbong.gymjjak.notification.domain.model.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> saveAll(List<Notification> notifications);

    Optional<Notification> findById(Long notificationId);

    List<Notification> findAllById(List<Long> notificationIds);

    // softDelete 된 알림 중 기준일 이상 알림 조회
    List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize);

    // 실제 hardDelete 하는 메서드
    int hardDeleteByIds(List<Long> ids);


}
