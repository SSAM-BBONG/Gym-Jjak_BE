package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

// 중복 신청 예외
public class DuplicateTrainerApplicationException extends ConflictException {

    public DuplicateTrainerApplicationException(Long applicantUserId) {
        super(TrainerApplicationErrorCode.TRAINER_APPLICATION_ALREADY_EXISTS);
        addContext("applicantUserId", applicantUserId);
    }
}
