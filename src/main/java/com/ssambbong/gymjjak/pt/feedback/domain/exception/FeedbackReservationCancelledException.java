package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class FeedbackReservationCancelledException extends ConflictException {
    public FeedbackReservationCancelledException() {
        super(FeedbackErrorCode.FEEDBACK_RESERVATION_CANCELLED);
    }
}
