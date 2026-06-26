package com.ssambbong.gymjjak.notification.application.command;

import java.util.List;

public record DeleteNotificationsCommand(
        Long requesterId,
        List<Long> notificationIds
) {
}
