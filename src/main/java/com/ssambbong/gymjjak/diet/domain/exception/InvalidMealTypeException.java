package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InvalidMealTypeException extends BadRequestException {
    public InvalidMealTypeException(String mealType) {
        super(MealAnalysisErrorCode.INVALID_MEAL_TYPE);
        addContext("mealType", mealType);
    }
}
