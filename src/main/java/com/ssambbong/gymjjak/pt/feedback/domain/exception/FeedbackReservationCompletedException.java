package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class FeedbackReservationCompletedException extends ConflictException {
    public FeedbackReservationCompletedException() {
        super(FeedbackErrorCode.FEEDBACK_RESERVATION_COMPLETED);
    }
}
