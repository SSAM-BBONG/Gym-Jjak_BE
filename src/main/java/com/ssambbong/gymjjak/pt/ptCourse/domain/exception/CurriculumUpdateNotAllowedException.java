package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class CurriculumUpdateNotAllowedException extends BadRequestException {
    public CurriculumUpdateNotAllowedException() {
        super(PtCourseErrorCode.CURRICULUM_UPDATE_NOT_ALLOWED,
                PtCourseErrorCode.CURRICULUM_UPDATE_NOT_ALLOWED.getMessage());
    }
}
