package com.ssambbong.gymjjak.calendar.application.service;

import com.ssambbong.gymjjak.calendar.application.command.CreateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.command.UpdateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.port.in.WorkoutDiaryUsecase;
import com.ssambbong.gymjjak.calendar.application.port.out.CalendarCacheEvictionPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPortToCategory;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutDiaryService implements WorkoutDiaryUsecase {

    private final WorkoutDiaryPort workoutDiaryPort;
    private final WorkoutDiaryPortToCategory workoutDiaryPortToCategory;
    private final CalendarCacheEvictionPort calendarCacheEvictionPort;

    @Override
    public Long createWorkoutDiary(
            Long userId,
            CreateWorkoutDiaryCommand command
    ) {

        log.debug("event=workoutDiary_create_start userId={}", userId);

        if (userId == null) {
            throw new CalendarException(CalendarErrorCode.USER_ID_REQUIRED);
        }

        if (command.diaryDate() == null) {
            throw new CalendarException(CalendarErrorCode.DIARY_DATE_REQUIRED);
        }

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

        try {
            Long workoutDiaryId = workoutDiaryPort.saveWorkoutDiary(workoutDiary);

            calendarCacheEvictionPort.evictMonth(
                    userId,
                    command.diaryDate()
            );

            log.debug(
                    "event=workoutDiary_create_succeed userId={} workoutDiaryId={}",
                    userId,
                    workoutDiaryId
            );

            return workoutDiaryId;
        } catch (DataIntegrityViolationException ex) {
            throw new CalendarException(CalendarErrorCode.DIARY_ALREADY_EXISTS);
        }
    }

    @Override
    public void updateWorkoutDiary(
            Long userId,
            Long workoutDiaryId,
            UpdateWorkoutDiaryCommand command
    ) {

        log.debug("event=workoutDiary_update_start userId={}", userId);

        LocalDate diaryDate = workoutDiaryPort.findDiaryDateByUserIdAndWorkoutDiaryId(
                userId,
                workoutDiaryId
        );

        Long categoryId = workoutDiaryPortToCategory.findCategoryIdByName(command.categoryName());

        workoutDiaryPort.updateWorkoutDiary(
                userId,
                workoutDiaryId,
                categoryId,
                command.title(),
                command.content()
        );

        calendarCacheEvictionPort.evictMonth(
                userId,
                diaryDate
        );

        log.debug("event=workoutDiary_update_succeed userId={}", userId);
    }

    @Override
    public void deleteWorkoutDiary(
            Long userId,
            Long workoutDiaryId
    ) {
        LocalDate diaryDate = workoutDiaryPort.findDiaryDateByUserIdAndWorkoutDiaryId(
                userId,
                workoutDiaryId
        );

        log.debug("event=workoutDiary_delete_start userId={}", userId);

        boolean exists = workoutDiaryPort.existsByIdAndUserId(
                workoutDiaryId,
                userId
        );
        if (!exists) {
            throw new CalendarException(CalendarErrorCode.DIARY_NOT_FOUND);
        }
        workoutDiaryPort.deleteWorkoutDiary(
                userId,
                workoutDiaryId
        );

        calendarCacheEvictionPort.evictMonth(
                userId,
                diaryDate
        );

        log.debug("event=workoutDiary_delete_succeed userId={}", userId);
    }

}
