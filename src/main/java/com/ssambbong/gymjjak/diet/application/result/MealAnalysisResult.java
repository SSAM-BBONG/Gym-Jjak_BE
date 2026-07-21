package com.ssambbong.gymjjak.diet.application.result;

import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import com.ssambbong.gymjjak.diet.domain.model.MealType;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record MealAnalysisResult(
        Long mealId,
        MealType mealType,
        LocalDateTime mealTime,
        String menu,
        Long kcal,
        BigDecimal carbohydrate,
        BigDecimal protein,
        BigDecimal fat,
        Long fileId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MealAnalysisResult from(MealAnalysis meal) {
        return new MealAnalysisResult(meal.getId(), meal.getMealType(), meal.getMealTime(),
                meal.getMenu(), meal.getKcal(), meal.getCarbohydrate(), meal.getProtein(), meal.getFat(),
                meal.getFileId(), meal.getCreatedAt(), meal.getUpdatedAt());
    }
}
