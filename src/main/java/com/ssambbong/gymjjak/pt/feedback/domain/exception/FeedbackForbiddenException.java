package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class FeedbackForbiddenException extends ForbiddenException {
    public FeedbackForbiddenException() {
        super(FeedbackErrorCode.FEEDBACK_FORBIDDEN,
                FeedbackErrorCode.FEEDBACK_FORBIDDEN.getMessage());
    }
}
