package com.ssambbong.gymjjak.inbody.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

@Schema(description = "인바디 측정 기록 목록 조회 요청")
public record GetInbodyListRequest(
        @Schema(
                description = "이전 응답 목록의 마지막 측정일. 첫 조회 시 미입력",
                example = "2026-07-03",
                nullable = true
        )
        LocalDate measuredDate,

        @Schema(
                description = "이전 응답 목록의 마지막 인바디 ID. 첫 조회 시 미입력",
                example = "21",
                nullable = true
        )
        @Min(value = 1, message = "인바디 ID는 1 이상이어야 합니다.")
        Long inbodyId
) {
    @AssertTrue(message = "측정일과 인바디 ID 커서는 함께 입력해야 합니다.")
    @Schema(hidden = true)
    public boolean isCursorValid() {
        return (measuredDate == null) == (inbodyId == null);
    }
}
