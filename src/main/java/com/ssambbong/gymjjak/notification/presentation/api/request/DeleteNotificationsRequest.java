package com.ssambbong.gymjjak.notification.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DeleteNotificationsRequest(

        @Schema(
                description = "삭제할 알림 ID 목록, 단일, 다중 모두 가능함",
                example = "[1, 2, 3]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "삭제할 알림ID 는 필수 입니다.")
        @Size(max = 100, message = "알림은 한 번에 최대 100개까지 삭제할 수 있습니다.")
        List<
                @NotNull(message = "알림 ID는 필수입니다.")
                @Positive(message = "알림 ID는 1 이상이어야 합니다.")
                Long> notificationIds
) {
}
