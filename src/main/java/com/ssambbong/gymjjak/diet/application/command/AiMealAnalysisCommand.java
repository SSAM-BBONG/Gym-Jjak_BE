package com.ssambbong.gymjjak.diet.application.command;

import com.ssambbong.gymjjak.diet.domain.model.MealType;

import java.time.LocalDateTime;

public record AiMealAnalysisCommand(
        Long userId,
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize,
        MealType mealType,
        LocalDateTime mealTime
) {
}
