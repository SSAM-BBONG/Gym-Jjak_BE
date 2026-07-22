package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class MealAccessDeniedException extends ForbiddenException {

    public MealAccessDeniedException() {
        super(MealAnalysisErrorCode.MEAL_ACCESS_DENIED,
                MealAnalysisErrorCode.MEAL_ACCESS_DENIED.getMessage());
    }
}
