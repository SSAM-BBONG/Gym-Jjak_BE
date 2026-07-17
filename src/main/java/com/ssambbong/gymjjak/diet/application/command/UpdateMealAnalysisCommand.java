package com.ssambbong.gymjjak.diet.application.command;

import com.ssambbong.gymjjak.diet.domain.model.MealType;

import java.time.LocalDateTime;

public record UpdateMealAnalysisCommand(
        Long userId,
        MealType mealType,
        boolean mealTypePresent,
        LocalDateTime mealTime,
        boolean mealTimePresent,
        String menu,
        boolean menuPresent,
        Long kcal,
        boolean kcalPresent,
        Long fileId,
        boolean fileIdPresent
) {
}
