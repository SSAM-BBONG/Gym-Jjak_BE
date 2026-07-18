package com.ssambbong.gymjjak.diet.application.command;

import com.ssambbong.gymjjak.diet.domain.model.MealType;

import java.time.LocalDateTime;

public record MealAnalysisCommand(
        Long userId,
        MealType mealType,
        LocalDateTime mealTime,
        String menu,
        Long kcal,
        Long fileId
) {
}
