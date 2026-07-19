package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class DuplicateNutritionGoalException extends ConflictException {
    public DuplicateNutritionGoalException() { super(NutritionGoalErrorCode.GOAL_ALREADY_EXISTS); }
    public DuplicateNutritionGoalException(Throwable cause) {
        super(NutritionGoalErrorCode.GOAL_ALREADY_EXISTS, NutritionGoalErrorCode.GOAL_ALREADY_EXISTS.getMessage(), cause);
    }
}
