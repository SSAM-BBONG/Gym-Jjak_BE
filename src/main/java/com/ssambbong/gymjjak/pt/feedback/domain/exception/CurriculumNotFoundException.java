package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class CurriculumNotFoundException extends NotFoundException {
    public CurriculumNotFoundException() {
        super(FeedbackErrorCode.CURRICULUM_NOT_FOUND,
                FeedbackErrorCode.CURRICULUM_NOT_FOUND.getMessage());
    }
}
