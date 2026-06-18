package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.InvalidTrainerApplicationException;

public record ApproveTrainerApplicationCommand(
        Long trainerApplicationId,
        Long adminId
) {

    public ApproveTrainerApplicationCommand {
        if (trainerApplicationId == null || trainerApplicationId <= 0) {
            throw new InvalidTrainerApplicationException(
                    "trainerApplicationId는 1 이상이어야 합니다."
            );
        }

        if (adminId == null || adminId <= 0) {
            throw new InvalidTrainerApplicationException(
                    "adminId는 1 이상이어야 합니다."
            );
        }
    }
}
