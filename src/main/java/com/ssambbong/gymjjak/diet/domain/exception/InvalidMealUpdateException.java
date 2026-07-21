package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InvalidMealUpdateException extends BadRequestException {
    public InvalidMealUpdateException() {
        super(MealAnalysisErrorCode.INVALID_UPDATE_REQUEST);
    }
}
