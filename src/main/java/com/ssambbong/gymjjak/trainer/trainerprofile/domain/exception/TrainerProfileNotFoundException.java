package com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerProfileNotFoundException extends NotFoundException {

    public TrainerProfileNotFoundException(String identifierName, Long identifier) {
        super(TrainerProfileErrorCode.TRAINER_PROFILE_NOT_FOUND);
        addContext(identifierName, identifier);
    }
}
