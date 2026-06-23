package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PtCourseHasActiveReservationException extends ConflictException {
    public PtCourseHasActiveReservationException() {
        super(PtCourseErrorCode.PT_COURSE_HAS_ACTIVE_RESERVATION,
                PtCourseErrorCode.PT_COURSE_HAS_ACTIVE_RESERVATION.getMessage());
    }
}
