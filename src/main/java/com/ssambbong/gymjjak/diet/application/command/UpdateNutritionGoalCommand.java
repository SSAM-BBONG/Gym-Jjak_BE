package com.ssambbong.gymjjak.diet.application.command;

public record UpdateNutritionGoalCommand(
        Long userId,
        Long goalProtein, boolean proteinPresent,
        Long goalCarbohydrate, boolean carbohydratePresent,
        Long goalFat, boolean fatPresent,
        Long dailyGoalKcal, boolean kcalPresent) {}
