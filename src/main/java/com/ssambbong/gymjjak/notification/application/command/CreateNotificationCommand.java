package com.ssambbong.gymjjak.notification.application.command;

import com.ssambbong.gymjjak.notification.domain.type.NotificationType;

import java.time.LocalDateTime;

public record CreateNotificationCommand(
        Long receiverId,
        NotificationType type,
        Long targetId,
        LocalDateTime eventAt
) {
}
