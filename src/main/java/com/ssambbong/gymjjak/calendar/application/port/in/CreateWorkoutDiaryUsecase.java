package com.ssambbong.gymjjak.calendar.application.port.in;

import com.ssambbong.gymjjak.calendar.application.command.CreateWorkoutDiaryCommand;

public interface CreateWorkoutDiaryUsecase {

    void createWorkoutDiary(
            Long userId,
            CreateWorkoutDiaryCommand command
    );
}
