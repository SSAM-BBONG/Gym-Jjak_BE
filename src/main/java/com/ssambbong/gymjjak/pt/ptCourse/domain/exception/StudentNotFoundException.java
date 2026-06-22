package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class StudentNotFoundException extends NotFoundException {
    public StudentNotFoundException() {
        super(PtCourseErrorCode.STUDENT_NOT_FOUND,
                PtCourseErrorCode.STUDENT_NOT_FOUND.getMessage());
    }
}
