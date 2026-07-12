package com.ssambbong.gymjjak.exercise.application.result;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

public record ExerciseResult(
        Long exerciseId,
        PartType part,
        String exerciseName
) {
}
