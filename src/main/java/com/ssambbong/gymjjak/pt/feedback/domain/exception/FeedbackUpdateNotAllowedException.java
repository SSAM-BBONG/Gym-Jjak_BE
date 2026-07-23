package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class FeedbackUpdateNotAllowedException extends ConflictException {
    public FeedbackUpdateNotAllowedException() {
        super(FeedbackErrorCode.FEEDBACK_UPDATE_NOT_ALLOWED);
    }
}
