package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class MealAnalysisNotFoundException extends NotFoundException {
    public MealAnalysisNotFoundException(Long mealId) {
        super(MealAnalysisErrorCode.MEAL_NOT_FOUND);
        addContext("mealId", mealId);
    }
}
