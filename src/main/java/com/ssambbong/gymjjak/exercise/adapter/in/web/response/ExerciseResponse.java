package com.ssambbong.gymjjak.exercise.adapter.in.web.response;

import com.ssambbong.gymjjak.exercise.application.result.ExerciseResult;
import com.ssambbong.gymjjak.global.presentation.api.common.PartTypeNameMapper;

import java.time.LocalDateTime;

public record ExerciseResponse(
        Long exerciseId,
        String part,
        String exerciseName,
        LocalDateTime createdAt
) {
    public static ExerciseResponse from(ExerciseResult result) {
        return new ExerciseResponse(
                result.exerciseId(),
                PartTypeNameMapper.toKoreanName(result.part()),
                result.exerciseName(),
                result.createdAt()
        );
    }
}
