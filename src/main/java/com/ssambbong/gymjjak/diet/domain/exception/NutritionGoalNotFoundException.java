package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class NutritionGoalNotFoundException extends NotFoundException {
    public NutritionGoalNotFoundException() { super(NutritionGoalErrorCode.GOAL_NOT_FOUND); }
}
