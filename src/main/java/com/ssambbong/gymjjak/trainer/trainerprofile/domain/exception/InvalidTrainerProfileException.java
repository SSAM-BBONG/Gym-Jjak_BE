package com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InvalidTrainerProfileException extends BadRequestException {

    public InvalidTrainerProfileException(String message) {
        super(
                TrainerProfileErrorCode.TRAINER_PROFILE_INVALID_REQUEST,
                message
        );
    }
}
