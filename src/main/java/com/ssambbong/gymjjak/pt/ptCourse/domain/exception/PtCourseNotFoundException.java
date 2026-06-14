package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class PtCourseNotFoundException extends NotFoundException {
    public PtCourseNotFoundException() {
        super(PtCourseErrorCode.PT_COURSE_NOT_FOUND,
                PtCourseErrorCode.PT_COURSE_NOT_FOUND.getMessage());
    }
}
