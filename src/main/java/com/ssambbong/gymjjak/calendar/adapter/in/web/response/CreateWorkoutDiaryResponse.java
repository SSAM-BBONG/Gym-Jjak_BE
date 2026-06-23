package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

public record CreateWorkoutDiaryResponse(
        Long workoutDiaryId
) {
    public static CreateWorkoutDiaryResponse from(Long workoutDiaryId) {
        return new CreateWorkoutDiaryResponse(workoutDiaryId);
    }
}
