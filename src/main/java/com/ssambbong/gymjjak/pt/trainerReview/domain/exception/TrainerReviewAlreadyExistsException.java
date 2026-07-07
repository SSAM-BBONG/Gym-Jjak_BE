package com.ssambbong.gymjjak.pt.trainerReview.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class TrainerReviewAlreadyExistsException extends ConflictException {

    public TrainerReviewAlreadyExistsException() {
        super(TrainerReviewErrorCode.TRAINER_REVIEW_ALREADY_EXISTS);
    }

    public TrainerReviewAlreadyExistsException(Throwable cause) {
        super(TrainerReviewErrorCode.TRAINER_REVIEW_ALREADY_EXISTS,
                TrainerReviewErrorCode.TRAINER_REVIEW_ALREADY_EXISTS.getMessage(), cause);
    }
}
