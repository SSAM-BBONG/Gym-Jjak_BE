package com.ssambbong.gymjjak.calendar.application.result;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

public record CalendarExerciseSnapshot(
        Long exerciseId,
        PartType part,
        String exerciseName
) {
}
