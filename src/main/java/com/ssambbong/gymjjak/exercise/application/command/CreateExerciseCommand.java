package com.ssambbong.gymjjak.exercise.application.command;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

public record CreateExerciseCommand(
        PartType part,
        String exerciseName
) {
}
