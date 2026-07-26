package com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;

public class TrainerRoutineRecommendationException extends ApplicationException {
    public TrainerRoutineRecommendationException(TrainerRoutineRecommendationErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
    public TrainerRoutineRecommendationException(TrainerRoutineRecommendationErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
