package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class PtCourseRequestInvalidException extends BadRequestException {

    public PtCourseRequestInvalidException() {
        super(PtCourseErrorCode.PT_COURSE_REQUEST_INVALID,
                PtCourseErrorCode.PT_COURSE_REQUEST_INVALID.getMessage());
    }
}
