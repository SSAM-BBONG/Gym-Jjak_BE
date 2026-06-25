package com.ssambbong.gymjjak.notification.application.result;

import lombok.Builder;

import java.util.List;

@Builder
public record MarkNotificationReadResult(
        List<Long> readNotificationIds
) {
}
