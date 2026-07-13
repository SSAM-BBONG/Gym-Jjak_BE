package com.ssambbong.gymjjak.calendar.application.command;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.util.List;

public record UpdateWorkoutDiaryCommand(
        PartType part,
        Long exerciseId,
        List<WorkoutDiarySetCommand> sets
) {
}
