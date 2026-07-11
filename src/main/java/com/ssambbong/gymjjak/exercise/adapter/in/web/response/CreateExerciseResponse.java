package com.ssambbong.gymjjak.exercise.adapter.in.web.response;

public record CreateExerciseResponse(
        Long exerciseId
) {
    public static CreateExerciseResponse from(Long exerciseId) {
        return new CreateExerciseResponse(exerciseId);
    }
}
