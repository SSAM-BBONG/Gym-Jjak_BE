package com.ssambbong.gymjjak.diet.application.port.out;

import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import java.util.Optional;

public interface NutritionGoalPort {
    NutritionGoal save(NutritionGoal goal);
    Optional<NutritionGoal> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    int deleteByUserId(Long userId);
}
