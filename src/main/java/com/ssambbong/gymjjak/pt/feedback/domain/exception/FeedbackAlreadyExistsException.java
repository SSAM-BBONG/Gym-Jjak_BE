package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class FeedbackAlreadyExistsException extends ConflictException {
    public FeedbackAlreadyExistsException() {
        super(FeedbackErrorCode.FEEDBACK_ALREADY_EXISTS,
                FeedbackErrorCode.FEEDBACK_ALREADY_EXISTS.getMessage());
    }
}
