package com.ssambbong.gymjjak.notification.presentation.api.response;

import com.ssambbong.gymjjak.notification.application.result.NotificationResult;
import com.ssambbong.gymjjak.notification.domain.type.NotificationCategory;
import com.ssambbong.gymjjak.notification.domain.type.NotificationTargetType;
import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long notificationId,
        NotificationCategory category,
        String categoryLabel,
        NotificationType type,
        String title,
        String content,
        NotificationTargetType targetType,
        Long targetId,
        LocalDateTime eventAt,
        boolean read
) {

    public static NotificationResponse from(NotificationResult result) {
        return NotificationResponse.builder()
                .notificationId(result.notificationId())
                .category(result.category())
                .categoryLabel(result.categoryLabel())
                .type(result.type())
                .title(result.title())
                .content(result.content())
                .targetType(result.targetType())
                .targetId(result.targetId())
                .eventAt(result.eventAt())
                .read(result.read())
                .build();
    }
}
