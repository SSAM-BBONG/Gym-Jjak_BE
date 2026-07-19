package com.ssambbong.gymjjak.diet.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "식단 정보 응답")
public record MealAnalysisResponse(
        @Schema(description = "식단 ID", example = "1")
        Long mealId,
        @Schema(description = "식사 유형", example = "아침", allowableValues = {"아침", "점심", "저녁", "간식"})
        String mealType,
        @Schema(description = "식사 일시", example = "2026-07-18 08:30", type = "string")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime mealTime,
        @Schema(description = "먹은 메뉴", example = "삶은 계란 2개와 바나나")
        String menu,
        @Schema(description = "섭취 열량(kcal)", example = "280", nullable = true)
        Long kcal,
        @Schema(description = "탄수화물(g)", example = "67.00", nullable = true)
        BigDecimal carbohydrate,
        @Schema(description = "단백질(g)", example = "51.90", nullable = true)
        BigDecimal protein,
        @Schema(description = "지방(g)", example = "7.60", nullable = true)
        BigDecimal fat,
        @Schema(description = "식단 사진의 파일 ID", example = "15", nullable = true)
        Long fileId,
        @Schema(description = "등록 일시", example = "2026-07-18T08:35:12")
        LocalDateTime createdAt,
        @Schema(description = "마지막 수정 일시", example = "2026-07-18T09:10:00")
        LocalDateTime updatedAt
) {
}
