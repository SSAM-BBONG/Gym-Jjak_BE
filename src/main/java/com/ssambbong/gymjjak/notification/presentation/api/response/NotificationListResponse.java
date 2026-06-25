package com.ssambbong.gymjjak.notification.presentation.api.response;

import com.ssambbong.gymjjak.notification.application.result.NotificationListResult;
import lombok.Builder;

import java.util.List;

@Builder
public record NotificationListResponse(
        List<NotificationResponse> content,
        int page,
        int size,
        boolean hasNext
) {
    public static NotificationListResponse from(NotificationListResult result) {
        return NotificationListResponse.builder()
                .content(
                        result.content().stream()
                                .map(NotificationResponse::from)
                                .toList()
                )
                .page(result.page())
                .size(result.size())
                .hasNext(result.hasNext())
                .build();
    }
}
