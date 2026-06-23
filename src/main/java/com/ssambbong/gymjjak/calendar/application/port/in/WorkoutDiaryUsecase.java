package com.ssambbong.gymjjak.calendar.application.port.in;

import com.ssambbong.gymjjak.calendar.application.command.CreateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.command.UpdateWorkoutDiaryCommand;

public interface WorkoutDiaryUsecase {

    void createWorkoutDiary(
            Long userId,
            CreateWorkoutDiaryCommand command
    );

    Long updateWorkoutDiary(
            Long userId,
            Long workoutDiaryId,
            UpdateWorkoutDiaryCommand command
    );
}
