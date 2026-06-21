package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class ForbiddenTrainerApplicationCancelException extends ForbiddenException {

    public ForbiddenTrainerApplicationCancelException(
            Long requesterId, Long trainerApplicationId
    ) {
        super(
                TrainerApplicationErrorCode.TRAINER_APPLICATION_CANCEL_ACCESS_DENIED,
                TrainerApplicationErrorCode.TRAINER_APPLICATION_CANCEL_ACCESS_DENIED.getMessage()
        );

        addContext("requesterId", requesterId);
        addContext("trainerApplicationId", trainerApplicationId);
    }
}
