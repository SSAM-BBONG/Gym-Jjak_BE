package com.ssambbong.gymjjak.calendar.application.port.out;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDiaryResult;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiary;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutDiaryPort {

    boolean existsByUserIdAndDiaryDate(
            Long userId,
            LocalDate diaryDate
    );

    void saveWorkoutDiary(WorkoutDiary workoutDiary);

    void updateWorkoutDiary(
            Long userId,
            Long workoutDiaryId,
            Long categoryId,
            String title,
            String content
    );

    boolean existsByIdAndUserId(
            Long workoutDiaryId,
            Long userId
    );

    void deleteWorkoutDiary(
            Long userId,
            Long workoutDiaryId
    );

    Optional<CalendarDayDiaryResult> findDiaryByUserIdAndDate(
            Long userId,
            LocalDate date
    );

    List<CalendarMonthDiaryResult> findDiaryTitlesByUserIdAndPeriod(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );


}
