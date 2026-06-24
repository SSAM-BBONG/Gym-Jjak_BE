package com.ssambbong.gymjjak.notification.application.result;


import java.util.List;

public record NotificationListResult(
        List<NotificationResult> content,
        int page,
        int size,
        boolean hasNext
) {
}
