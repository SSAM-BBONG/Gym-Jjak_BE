package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerApplicationNotFoundException extends NotFoundException {

    public TrainerApplicationNotFoundException(Long trainerApplicationId) {
        super(TrainerApplicationErrorCode.TRAINER_APPLICATION_NOT_FOUND);
        addContext("trainerApplicationId", trainerApplicationId);
    }
}