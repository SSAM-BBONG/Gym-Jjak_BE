package com.ssambbong.gymjjak.notification.presentation.api.response;

import com.ssambbong.gymjjak.notification.application.result.UnreadNotificationCountResult;
import lombok.Builder;

@Builder
public record UnreadNotificationCountResponse(
        long unreadCount
) {

    public static UnreadNotificationCountResponse from(UnreadNotificationCountResult result) {
        return UnreadNotificationCountResponse.builder()
                .unreadCount(result.unreadCount())
                .build();
    }
}
