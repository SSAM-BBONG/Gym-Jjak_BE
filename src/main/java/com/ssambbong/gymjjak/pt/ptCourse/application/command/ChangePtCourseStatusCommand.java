package com.ssambbong.gymjjak.pt.ptCourse.application.command;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;

public record ChangePtCourseStatusCommand(
        Long userId,
        Long ptCourseId,
        PtCourseStatus status
) {
}
