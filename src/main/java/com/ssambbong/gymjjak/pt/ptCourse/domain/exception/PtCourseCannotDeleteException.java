package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PtCourseCannotDeleteException extends ConflictException {
    public PtCourseCannotDeleteException() {
        super(PtCourseErrorCode.PT_COURSE_CANNOT_DELETE,
                PtCourseErrorCode.PT_COURSE_CANNOT_DELETE.getMessage());
    }
}
