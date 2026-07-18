package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class AiNutritionAccessRequiredException extends ForbiddenException {
    public AiNutritionAccessRequiredException() {
        super(MealAnalysisErrorCode.AI_NUTRITION_ACCESS_REQUIRED,
                MealAnalysisErrorCode.AI_NUTRITION_ACCESS_REQUIRED.getMessage());
    }
}
