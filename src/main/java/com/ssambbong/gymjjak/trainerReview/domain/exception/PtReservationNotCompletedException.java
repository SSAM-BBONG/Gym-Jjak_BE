package com.ssambbong.gymjjak.trainerReview.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class PtReservationNotCompletedException extends BadRequestException {

    public PtReservationNotCompletedException() {
        super(TrainerReviewErrorCode.PT_RESERVATION_NOT_COMPLETED);
    }
}
