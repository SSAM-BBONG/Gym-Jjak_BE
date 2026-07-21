package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PtCourseHasActiveReservationForHideException extends ConflictException {
    public PtCourseHasActiveReservationForHideException() {
        super(PtCourseErrorCode.PT_COURSE_HAS_ACTIVE_RESERVATION_FOR_HIDE,
                PtCourseErrorCode.PT_COURSE_HAS_ACTIVE_RESERVATION_FOR_HIDE.getMessage());
    }
}
