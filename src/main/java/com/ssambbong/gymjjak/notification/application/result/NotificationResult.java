package com.ssambbong.gymjjak.notification.application.result;

import com.ssambbong.gymjjak.notification.domain.model.Notification;
import com.ssambbong.gymjjak.notification.domain.type.NotificationCategory;
import com.ssambbong.gymjjak.notification.domain.type.NotificationTargetType;
import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResult(
        Long notificationId,
        Long receiverId,
        NotificationType type,
        NotificationCategory category,
        String categoryLabel,
        String title,
        String content,
        NotificationTargetType targetType,
        Long targetId,
        LocalDateTime eventAt,
        boolean read
) {
    public static NotificationResult from(
            Notification domain) {
        return NotificationResult.builder()
                .notificationId(domain.getNotificationId())
                .receiverId(domain.getReceiverId())
                .type(domain.getType())
                .category(domain.getCategory())
                .categoryLabel(domain.getCategory().getLabel())
                .title(domain.getTitle())
                .content(domain.getContent())
                .targetType(domain.getTargetType())
                .targetId(domain.getTargetId())
                .eventAt(domain.getEventAt())
                .read(domain.isRead())
                .build();
    }
}
