package com.ssambbong.gymjjak.diet.application.command;

public record NutritionGoalCommand(Long userId, Long goalProtein, Long goalCarbohydrate,
                                   Long goalFat, Long dailyGoalKcal) {}
