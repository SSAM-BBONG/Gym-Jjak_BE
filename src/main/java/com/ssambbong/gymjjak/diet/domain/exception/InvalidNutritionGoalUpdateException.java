package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InvalidNutritionGoalUpdateException extends BadRequestException {
    public InvalidNutritionGoalUpdateException() { super(NutritionGoalErrorCode.INVALID_UPDATE_REQUEST); }
}
