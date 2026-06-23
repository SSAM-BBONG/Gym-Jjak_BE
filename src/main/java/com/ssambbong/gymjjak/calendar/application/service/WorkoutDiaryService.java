package com.ssambbong.gymjjak.calendar.application.service;

import com.ssambbong.gymjjak.calendar.application.command.CreateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.port.in.CreateWorkoutDiaryUsecase;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPortToCategory;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutDiaryService implements CreateWorkoutDiaryUsecase {

    private final WorkoutDiaryPort workoutDiaryPort;
    private final WorkoutDiaryPortToCategory workoutDiaryPortToCategory;

    @Override
    public void createWorkoutDiary(
            Long userId,
            CreateWorkoutDiaryCommand command
    ) {
        if (workoutDiaryPort.existsByUserIdAndDiaryDate(userId, command.diaryDate())) {
            throw new CalendarException(CalendarErrorCode.DIARY_ALREADY_EXISTS);
        }

        Long categoryId = workoutDiaryPortToCategory.findCategoryIdByName(
                command.categoryName()
        );

        WorkoutDiary workoutDiary = WorkoutDiary.create(
                userId,
                categoryId,
                command.title(),
                command.content(),
                command.diaryDate()
        );

        workoutDiaryPort.saveWorkoutDiary(workoutDiary);
    }
}
