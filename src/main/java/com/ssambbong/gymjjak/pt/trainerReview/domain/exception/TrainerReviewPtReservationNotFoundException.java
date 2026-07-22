package com.ssambbong.gymjjak.pt.trainerReview.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerReviewPtReservationNotFoundException extends NotFoundException {

    public TrainerReviewPtReservationNotFoundException() {
        super(TrainerReviewErrorCode.PT_RESERVATION_NOT_FOUND);
    }
}
