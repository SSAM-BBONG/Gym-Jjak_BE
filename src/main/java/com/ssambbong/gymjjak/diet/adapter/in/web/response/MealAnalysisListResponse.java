package com.ssambbong.gymjjak.diet.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "식단 목록 항목 응답")
public record MealAnalysisListResponse(
        @Schema(description = "식단 ID", example = "1")
        Long mealId,
        @Schema(description = "식사 유형", example = "아침")
        String mealType,
        @Schema(description = "식사 일시", example = "2026-07-18 08:30", type = "string")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime mealTime,
        @Schema(description = "먹은 메뉴", example = "현미밥과 닭가슴살")
        String menu
) {
}
