package com.ssambbong.gymjjak.diet.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "저장된 식단과 AI 분석 정보")
public record AiMealAnalysisResponse(
        Long mealId,
        String mealType,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime mealTime,
        String menu,
        Long fileId,
        Long kcal,
        BigDecimal carbohydrate,
        BigDecimal protein,
        BigDecimal fat,
        String evaluation,
        BigDecimal confidence,
        List<String> warnings,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
