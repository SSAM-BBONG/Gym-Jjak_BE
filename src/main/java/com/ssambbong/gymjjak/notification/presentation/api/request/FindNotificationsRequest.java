package com.ssambbong.gymjjak.notification.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FindNotificationsRequest(

        @Schema(description = "페이지 번호. 시작값: 0", example = "0")
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,

        @Schema(description = "한 번에 조회할 알림 개수. 기본값: 10", example = "10")
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 50, message = "size는 최대 50까지 가능합니다.")
        Integer size
) {

    public int resolvePage() {
        return page == null ? 0 : page;
    }
    public int resolveSize() {
        return size == null ? 10 : size;
    }
}
