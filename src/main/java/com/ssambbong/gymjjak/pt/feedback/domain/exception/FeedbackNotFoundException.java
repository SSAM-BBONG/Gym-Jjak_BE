package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class FeedbackNotFoundException extends NotFoundException {
    public FeedbackNotFoundException() {
        super(FeedbackErrorCode.FEEDBACK_NOT_FOUND,
                FeedbackErrorCode.FEEDBACK_NOT_FOUND.getMessage());
    }
}
