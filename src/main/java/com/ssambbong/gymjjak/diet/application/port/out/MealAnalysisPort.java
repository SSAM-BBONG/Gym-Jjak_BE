package com.ssambbong.gymjjak.diet.application.port.out;

import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import com.ssambbong.gymjjak.diet.application.result.MealPageResult;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;

import java.util.Optional;

public interface MealAnalysisPort {
    MealAnalysis save(MealAnalysis mealAnalysis);
    Optional<MealAnalysis> findByIdAndUserId(Long mealId, Long userId);
    MealPageResult<MealAnalysis> findAllByUserId(MealPageQuery query);
    int deleteByIdAndUserId(Long mealId, Long userId);
}
