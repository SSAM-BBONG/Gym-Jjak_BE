package com.ssambbong.gymjjak.calendar.application.service;

import com.ssambbong.gymjjak.calendar.application.command.CreateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.command.UpdateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.port.in.WorkoutDiaryUsecase;
import com.ssambbong.gymjjak.calendar.application.port.out.CalendarCacheEvictionPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutDiaryService implements WorkoutDiaryUsecase {

    private final WorkoutDiaryPort workoutDiaryPort;
    private final CalendarCacheEvictionPort calendarCacheEvictionPort;

    @Override
    public Long createWorkoutDiary(Long userId, CreateWorkoutDiaryCommand command) {
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

        WorkoutDiary workoutDiary = WorkoutDiary.create(
                userId,
                command.title(),
                command.content(),
                command.diaryDate()
        );

        try {
            Long workoutDiaryId = workoutDiaryPort.saveWorkoutDiary(workoutDiary);
            evictMonthAfterCommit(userId, command.diaryDate());
            log.debug("event=workoutDiary_create_succeed userId={} workoutDiaryId={}", userId, workoutDiaryId);
            return workoutDiaryId;
        } catch (DataIntegrityViolationException ex) {
            throw new CalendarException(CalendarErrorCode.DIARY_ALREADY_EXISTS);
        }
    }

    @Override
    public void updateWorkoutDiary(Long userId, Long workoutDiaryId, UpdateWorkoutDiaryCommand command) {
        log.debug("event=workoutDiary_update_start userId={}", userId);

        LocalDate diaryDate = workoutDiaryPort.findDiaryDateByUserIdAndWorkoutDiaryId(userId, workoutDiaryId);

        workoutDiaryPort.updateWorkoutDiary(userId, workoutDiaryId, command.title(), command.content());

        evictMonthAfterCommit(userId, diaryDate);
        log.debug("event=workoutDiary_update_succeed userId={}", userId);
    }

    @Override
    public void deleteWorkoutDiary(Long userId, Long workoutDiaryId) {
        LocalDate diaryDate = workoutDiaryPort.findDiaryDateByUserIdAndWorkoutDiaryId(userId, workoutDiaryId);

        log.debug("event=workoutDiary_delete_start userId={}", userId);

        if (!workoutDiaryPort.existsByIdAndUserId(workoutDiaryId, userId)) {
            throw new CalendarException(CalendarErrorCode.DIARY_NOT_FOUND);
        }
        workoutDiaryPort.deleteWorkoutDiary(userId, workoutDiaryId);

        evictMonthAfterCommit(userId, diaryDate);
        log.debug("event=workoutDiary_delete_succeed userId={}", userId);
    }

    private void evictMonthAfterCommit(Long userId, LocalDate date) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            calendarCacheEvictionPort.evictMonth(userId, date);
                        }
                    }
            );
            return;
        }
        calendarCacheEvictionPort.evictMonth(userId, date);
    }
}
