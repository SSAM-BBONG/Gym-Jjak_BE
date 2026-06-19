package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.InvalidTrainerApplicationException;

public record RejectTrainerApplicationCommand(

        Long trainerApplicationId,
        Long adminId,
        String rejectReason
) {

    public RejectTrainerApplicationCommand {

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

        if (rejectReason == null || rejectReason.isBlank()) {
            throw new InvalidTrainerApplicationException(
                    "반려 사유는 필수입니다."
            );
        }

        rejectReason = rejectReason.trim();
    }
}
