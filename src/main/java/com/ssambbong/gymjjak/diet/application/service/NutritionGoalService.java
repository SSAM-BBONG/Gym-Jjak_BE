package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.NutritionGoalCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateNutritionGoalCommand;
import com.ssambbong.gymjjak.diet.application.port.in.NutritionGoalUseCase;
import com.ssambbong.gymjjak.diet.application.port.out.NutritionGoalPort;
import com.ssambbong.gymjjak.diet.application.result.NutritionGoalResult;
import com.ssambbong.gymjjak.diet.domain.exception.DuplicateNutritionGoalException;
import com.ssambbong.gymjjak.diet.domain.exception.NutritionGoalNotFoundException;
import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NutritionGoalService implements NutritionGoalUseCase {
    private final NutritionGoalPort nutritionGoalPort;

    @Override @Transactional
    public NutritionGoalResult create(NutritionGoalCommand command) {
        if (nutritionGoalPort.existsByUserId(command.userId())) throw new DuplicateNutritionGoalException();
        NutritionGoal goal = NutritionGoal.create(command.userId(), command.goalProtein(),
                command.goalCarbohydrate(), command.goalFat(), command.dailyGoalKcal());
        return NutritionGoalResult.from(nutritionGoalPort.save(goal));
    }

    @Override @Transactional(readOnly = true)
    public NutritionGoalResult get(Long userId) { return NutritionGoalResult.from(getGoal(userId)); }

    @Override @Transactional
    public NutritionGoalResult update(UpdateNutritionGoalCommand command) {
        NutritionGoal goal = getGoal(command.userId());
        goal.update(command.goalProtein(), command.proteinPresent(),
                command.goalCarbohydrate(), command.carbohydratePresent(),
                command.goalFat(), command.fatPresent(), command.dailyGoalKcal(), command.kcalPresent());
        return NutritionGoalResult.from(nutritionGoalPort.save(goal));
    }

    @Override @Transactional
    public void delete(Long userId) {
        if (nutritionGoalPort.deleteByUserId(userId) == 0) throw new NutritionGoalNotFoundException();
    }

    private NutritionGoal getGoal(Long userId) {
        return nutritionGoalPort.findByUserId(userId).orElseThrow(NutritionGoalNotFoundException::new);
    }
}
