package com.ssambbong.gymjjak.calendar.application.port.out;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDiaryResult;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiary;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiarySet;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutDiaryPort {

    Long saveWorkoutDiary(WorkoutDiary workoutDiary);

    void updateWorkoutDiary(
            Long userId,
            Long workoutDiaryId,
            PartType part,
            String exercise,
            List<WorkoutDiarySet> sets
    );

    boolean existsByIdAndUserId(
            Long workoutDiaryId,
            Long userId
    );

    void deleteWorkoutDiary(
            Long userId,
            Long workoutDiaryId
    );

    List<CalendarDayDiaryResult> findDiariesByUserIdAndDate(
            Long userId,
            LocalDate date
    );

    List<CalendarMonthDiaryResult> findDiarySummariesByUserIdAndPeriod(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    LocalDate findDiaryDateByUserIdAndWorkoutDiaryId(
            Long userId,
            Long workoutDiaryId
    );
}
