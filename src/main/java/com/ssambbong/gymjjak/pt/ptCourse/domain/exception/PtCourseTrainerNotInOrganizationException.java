package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class PtCourseTrainerNotInOrganizationException extends ForbiddenException {
    public PtCourseTrainerNotInOrganizationException() {
        super(PtCourseErrorCode.PT_COURSE_TRAINER_NOT_IN_ORGANIZATION,
                PtCourseErrorCode.PT_COURSE_TRAINER_NOT_IN_ORGANIZATION.getMessage());
    }
}
