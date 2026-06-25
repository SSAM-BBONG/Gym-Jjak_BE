package com.ssambbong.gymjjak.notification.application.command;

import java.util.List;

public record MarkNotificationReadCommand(
        Long requesterId,
        List<Long> notificationIds
) {
}
