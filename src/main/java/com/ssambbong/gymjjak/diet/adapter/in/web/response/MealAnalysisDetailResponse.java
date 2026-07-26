package com.ssambbong.gymjjak.diet.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "식단 상세 정보 응답")
public record MealAnalysisDetailResponse(
        @Schema(description = "식단 ID", example = "1")
        Long mealId,
        @Schema(description = "식사 유형")
        String mealType,
        @Schema(description = "식사 일시", example = "2026-07-18 08:30", type = "string")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime mealTime,
        @Schema(description = "먹은 메뉴")
        String menu,
        Long kcal,
        BigDecimal carbohydrate,
        BigDecimal protein,
        BigDecimal fat,
        @Schema(description = "식단 이미지 Presigned URL", nullable = true)
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
