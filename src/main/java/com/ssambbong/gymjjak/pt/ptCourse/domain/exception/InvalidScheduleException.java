package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InvalidScheduleException extends BadRequestException {
    public InvalidScheduleException() {
        super(PtCourseErrorCode.INVALID_SCHEDULE,
                PtCourseErrorCode.INVALID_SCHEDULE.getMessage());
    }
}
