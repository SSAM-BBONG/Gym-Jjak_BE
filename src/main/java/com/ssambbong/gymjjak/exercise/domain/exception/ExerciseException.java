package com.ssambbong.gymjjak.exercise.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class ExerciseException extends ApplicationException {

    public ExerciseException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
