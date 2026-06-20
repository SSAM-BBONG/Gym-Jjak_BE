package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class PtCourseStatusInvalidException extends BadRequestException {
    public PtCourseStatusInvalidException() {
        super(PtCourseErrorCode.PT_COURSE_STATUS_INVALID,
                PtCourseErrorCode.PT_COURSE_STATUS_INVALID.getMessage());
    }
}
