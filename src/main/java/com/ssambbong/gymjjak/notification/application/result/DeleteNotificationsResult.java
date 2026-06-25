package com.ssambbong.gymjjak.notification.application.result;

import lombok.Builder;

import java.util.List;

@Builder
public record DeleteNotificationsResult(
        List<Long> deletedNotificationIds
) {
}
