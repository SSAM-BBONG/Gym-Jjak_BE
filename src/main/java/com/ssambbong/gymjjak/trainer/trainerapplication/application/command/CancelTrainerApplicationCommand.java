package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.InvalidTrainerApplicationException;

public record CancelTrainerApplicationCommand(
        Long trainerApplicationId,
        Long requesterId
) {

    public CancelTrainerApplicationCommand {

        if (trainerApplicationId == null || trainerApplicationId <= 0) {
            throw new InvalidTrainerApplicationException(
                    "trainerApplicationId는 1 이상이여야 합니다."
            );
        }

        if (requesterId == null || requesterId <= 0) {
            throw new InvalidTrainerApplicationException(
                    "requesterId는 1이상이여야합니다."
            );
        }
    }
}
