package com.ssambbong.gymjjak.trainerReview.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class TrainerReviewForbiddenException extends ForbiddenException {

    public TrainerReviewForbiddenException() {
        super(TrainerReviewErrorCode.TRAINER_REVIEW_FORBIDDEN,
                TrainerReviewErrorCode.TRAINER_REVIEW_FORBIDDEN.getMessage());
    }
}
