package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;

public class TrainerApplicationStatusConflictException extends ConflictException {

    public TrainerApplicationStatusConflictException(
            Long trainerApplicationId,
            TrainerApplicationStatus status
    ) {
        super(TrainerApplicationErrorCode.TRAINER_APPLICATION_STATUS_CONFLICT);
        addContext("trainerApplicationId", trainerApplicationId);
        addContext("status", status);
    }
}
