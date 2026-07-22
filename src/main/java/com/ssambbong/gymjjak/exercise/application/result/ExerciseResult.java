package com.ssambbong.gymjjak.exercise.application.result;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.time.LocalDateTime;

public record ExerciseResult(
        Long exerciseId,
        PartType part,
        String exerciseName,
        LocalDateTime createdAt
) {
}
