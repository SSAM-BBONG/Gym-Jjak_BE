package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

// 중복 신청 예외
public class DuplicateTrainerApplicationException extends ConflictException {

    public DuplicateTrainerApplicationException(Long applicantUserId) {
        super(TrainerApplicationErrorCode.TRAINER_APPLICATION_ALREADY_EXISTS);
        addContext("applicantUserId", applicantUserId);
    }

    public DuplicateTrainerApplicationException(Long applicantUserId, Throwable cause) {
        super(
                TrainerApplicationErrorCode.TRAINER_APPLICATION_ALREADY_EXISTS,
                TrainerApplicationErrorCode.TRAINER_APPLICATION_ALREADY_EXISTS.getMessage(),
                cause
        );
        addContext("applicantUserId", applicantUserId);
    }
}
