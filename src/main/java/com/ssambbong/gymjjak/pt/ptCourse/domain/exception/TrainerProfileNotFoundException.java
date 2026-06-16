package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerProfileNotFoundException extends NotFoundException {
    public TrainerProfileNotFoundException() {
        super(PtCourseErrorCode.TRAINER_PROFILE_NOT_FOUND,
                PtCourseErrorCode.TRAINER_PROFILE_NOT_FOUND.getMessage());
    }
}
