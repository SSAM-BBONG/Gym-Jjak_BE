package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BusinessException;

public class AiMealAnalysisException extends BusinessException {
    public AiMealAnalysisException(MealAnalysisErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public AiMealAnalysisException(MealAnalysisErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
