package com.ssambbong.gymjjak.diet.application.port.in;

import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MealAnalysisUseCase {
    MealAnalysisResult create(MealAnalysisCommand command);
    MealAnalysisResult get(Long userId, Long mealId);
    Page<MealAnalysisResult> getList(Long userId, Pageable pageable);
    MealAnalysisResult update(Long mealId, MealAnalysisCommand command);
    void delete(Long userId, Long mealId);
}
