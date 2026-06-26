package com.ssambbong.gymjjak.notification.presentation.api.response;

import com.ssambbong.gymjjak.notification.application.result.MarkNotificationReadResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "알림 읽음 처리 응답")
public record MarkNotificationReadResponse(
        @Schema(
                description = "읽음 처리된 알림 ID 목록",
                example = "[1, 2, 3]"
        )
        List<Long> readNotificationIds
) {

    public static MarkNotificationReadResponse from(MarkNotificationReadResult result) {
        return MarkNotificationReadResponse.builder()
                .readNotificationIds(result.readNotificationIds())
                .build();
    }
}
