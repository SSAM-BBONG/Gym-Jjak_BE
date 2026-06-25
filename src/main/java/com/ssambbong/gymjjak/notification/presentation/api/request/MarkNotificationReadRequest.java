package com.ssambbong.gymjjak.notification.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MarkNotificationReadRequest(

        @Schema(description = "읽기 처리할 알림 ID 목록", example = "[1, 3, 5]")
        @NotEmpty(message = "읽기 처리할 알림 ID는 필수입니다.")
        @Size( max = 100, message = "알림은 한 번에 최대 100개까지 읽음 처리할 수 있습니다.")
        List<
                @NotNull(message = "알림 ID는 필수입니다.")
                @Positive(message = "알림ID는 1 이상이여야 합니다.")
                Long> notificationIds
) {
}
