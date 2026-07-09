package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class ForbiddenTrainerApplicationReviewException extends ForbiddenException {

    public ForbiddenTrainerApplicationReviewException(
            Long requesterOrganizationId,
            Long trainerApplicationId
    ) {
        super(
                TrainerApplicationErrorCode.TRAINER_APPLICATION_REVIEW_ACCESS_DENIED,
                TrainerApplicationErrorCode.TRAINER_APPLICATION_REVIEW_ACCESS_DENIED.getMessage()
        );

        addContext("requesterOrganizationId", requesterOrganizationId);
        addContext("trainerApplicationId", trainerApplicationId);
    }
}
