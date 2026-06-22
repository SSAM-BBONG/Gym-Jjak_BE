package com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerProfileNotFoundException extends NotFoundException {

    public TrainerProfileNotFoundException(Long userId) {
        super(TrainerProfileErrorCode.TRAINER_PROFILE_NOT_FOUND);
        addContext("userId", userId);
    }
}
