package com.ssambbong.gymjjak.diet.application.result;

import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import java.time.LocalDateTime;

public record NutritionGoalResult(Long goalId, Long goalProtein, Long goalCarbohydrate,
                                  Long goalFat, Long dailyGoalKcal,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static NutritionGoalResult from(NutritionGoal goal) {
        return new NutritionGoalResult(goal.getId(), goal.getGoalProtein(), goal.getGoalCarbohydrate(),
                goal.getGoalFat(), goal.getDailyGoalKcal(), goal.getCreatedAt(), goal.getUpdatedAt());
    }
}
