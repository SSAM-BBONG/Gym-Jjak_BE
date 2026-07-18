package com.ssambbong.gymjjak.diet.application.command;

import com.ssambbong.gymjjak.diet.domain.model.MealType;

import java.time.LocalDateTime;

public record AiMealAnalysisCommand(
        Long userId,
        Long fileId,
        MealType mealType,
        LocalDateTime mealTime
) {
}
