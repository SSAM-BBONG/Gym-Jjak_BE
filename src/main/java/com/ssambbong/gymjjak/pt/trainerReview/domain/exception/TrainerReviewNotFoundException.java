package com.ssambbong.gymjjak.pt.trainerReview.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerReviewNotFoundException extends NotFoundException {

    public TrainerReviewNotFoundException() {
        super(TrainerReviewErrorCode.TRAINER_REVIEW_NOT_FOUND);
    }
}
