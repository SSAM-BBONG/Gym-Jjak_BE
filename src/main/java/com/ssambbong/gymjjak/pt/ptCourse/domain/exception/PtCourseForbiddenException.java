package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class PtCourseForbiddenException extends ForbiddenException {
    public PtCourseForbiddenException() {
        super(PtCourseErrorCode.PT_COURSE_FORBIDDEN,
                PtCourseErrorCode.PT_COURSE_FORBIDDEN.getMessage());
    }
}
