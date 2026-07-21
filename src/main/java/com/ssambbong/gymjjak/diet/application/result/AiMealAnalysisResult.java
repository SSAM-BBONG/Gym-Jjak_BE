package com.ssambbong.gymjjak.diet.application.result;

import com.ssambbong.gymjjak.diet.domain.model.MealType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AiMealAnalysisResult(
        Long mealId,
        MealType mealType,
        LocalDateTime mealTime,
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
