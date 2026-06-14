package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

// 잘못된 신청 요청 예외
public class InvalidTrainerApplicationException extends BadRequestException {

    public InvalidTrainerApplicationException(String reason) {
        super(TrainerApplicationErrorCode.TRAINER_APPLICATION_INVALID_REQUEST);
        addContext("reason", reason);
    }
}
