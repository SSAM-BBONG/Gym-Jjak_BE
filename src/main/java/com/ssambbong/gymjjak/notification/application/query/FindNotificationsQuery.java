package com.ssambbong.gymjjak.notification.application.query;

public record FindNotificationsQuery(
        Long receiverId,
        int page,
        int size
) {
}
