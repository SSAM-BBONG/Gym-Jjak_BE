package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class ForbiddenTrainerApplicationUpdateException extends ForbiddenException {

    public ForbiddenTrainerApplicationUpdateException(
            Long requesterId,
            Long trainerApplicationId
    ) {
        super(
                TrainerApplicationErrorCode.TRAINER_APPLICATION_ACCESS_DENIED,
                TrainerApplicationErrorCode.TRAINER_APPLICATION_ACCESS_DENIED.getMessage()
        );
        addContext("requesterId", requesterId);
        addContext("trainerApplicationId", trainerApplicationId);
    }
}
