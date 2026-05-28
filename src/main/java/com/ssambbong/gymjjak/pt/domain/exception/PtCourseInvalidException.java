package com.ssambbong.gymjjak.pt.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class PtCourseInvalidException extends BadRequestException {

    public PtCourseInvalidException() {
        super(PtCourseErrorCode.PT_COURSE_INVALID,
                PtCourseErrorCode.PT_COURSE_INVALID.getMessage());
    }
}
