package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class FeedbackMediaInvalidException extends BadRequestException {
    public FeedbackMediaInvalidException() {
        super(FeedbackErrorCode.FEEDBACK_MEDIA_INVALID);
    }
}
