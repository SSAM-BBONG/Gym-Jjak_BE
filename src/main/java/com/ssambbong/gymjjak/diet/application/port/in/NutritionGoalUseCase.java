package com.ssambbong.gymjjak.diet.application.port.in;

import com.ssambbong.gymjjak.diet.application.command.NutritionGoalCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateNutritionGoalCommand;
import com.ssambbong.gymjjak.diet.application.result.NutritionGoalResult;

public interface NutritionGoalUseCase {
    NutritionGoalResult create(NutritionGoalCommand command);
    NutritionGoalResult get(Long userId);
    NutritionGoalResult update(UpdateNutritionGoalCommand command);
    void delete(Long userId);
}
