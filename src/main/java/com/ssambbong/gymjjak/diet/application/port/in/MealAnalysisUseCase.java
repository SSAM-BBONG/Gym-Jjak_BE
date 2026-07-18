package com.ssambbong.gymjjak.diet.application.port.in;

import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import com.ssambbong.gymjjak.diet.application.result.MealPageResult;

public interface MealAnalysisUseCase {
    MealAnalysisResult create(MealAnalysisCommand command);
    MealAnalysisResult get(Long userId, Long mealId);
    MealPageResult<MealAnalysisResult> getList(MealPageQuery query);
    MealAnalysisResult update(Long mealId, UpdateMealAnalysisCommand command);
    void delete(Long userId, Long mealId);
}
