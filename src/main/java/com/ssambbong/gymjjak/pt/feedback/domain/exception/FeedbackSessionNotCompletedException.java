package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class FeedbackSessionNotCompletedException extends ConflictException {
    public FeedbackSessionNotCompletedException() {
        super(FeedbackErrorCode.FEEDBACK_SESSION_NOT_COMPLETED);
    }
}
