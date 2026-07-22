package com.ssambbong.gymjjak.diet.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NutritionGoal {
    private final Long id;
    private final Long userId;
    private Long goalProtein;
    private Long goalCarbohydrate;
    private Long goalFat;
    private Long dailyGoalKcal;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PUBLIC)
    private NutritionGoal(Long id, Long userId, Long goalProtein, Long goalCarbohydrate,
                          Long goalFat, Long dailyGoalKcal, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.goalProtein = goalProtein;
        this.goalCarbohydrate = goalCarbohydrate;
        this.goalFat = goalFat;
        this.dailyGoalKcal = dailyGoalKcal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static NutritionGoal create(Long userId, Long goalProtein, Long goalCarbohydrate,
                                       Long goalFat, Long dailyGoalKcal) {
        return NutritionGoal.builder().userId(userId).goalProtein(goalProtein)
                .goalCarbohydrate(goalCarbohydrate).goalFat(goalFat).dailyGoalKcal(dailyGoalKcal).build();
    }

    public void update(Long goalProtein, boolean proteinPresent,
                       Long goalCarbohydrate, boolean carbohydratePresent,
                       Long goalFat, boolean fatPresent,
                       Long dailyGoalKcal, boolean kcalPresent) {
        if (proteinPresent) this.goalProtein = goalProtein;
        if (carbohydratePresent) this.goalCarbohydrate = goalCarbohydrate;
        if (fatPresent) this.goalFat = goalFat;
        if (kcalPresent) this.dailyGoalKcal = dailyGoalKcal;
    }
}
