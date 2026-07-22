package com.ssambbong.gymjjak.calendar.application.service;

import com.ssambbong.gymjjak.calendar.application.port.in.CalendarUsecase;
import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPtReservationPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayPtResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthResult;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService implements CalendarUsecase {

    private final CalendarPtReservationPort calendarPtReservationPort;
    private final WorkoutDiaryPort workoutDiaryPort;
    private final CalendarMonthReader calendarMonthReader;

    @Override
    @Transactional(readOnly = true)
    public CalendarDayResult findCalendarDay(
            Long requesterUserId,
            Long targetUserId,
            LocalDate date
    ) {
        if (date == null) {
            throw new CalendarException(CalendarErrorCode.DATE_REQUIRED);
        }

        validateCalendarAccess(requesterUserId, targetUserId);

        log.debug("event=calendarDay_find_start userId={}", targetUserId);
        List<CalendarDayPtResult> pts =
                calendarPtReservationPort.findPtsByUserIdAndDate(
                        targetUserId,
                        date
                );

        List<CalendarDayDiaryResult> diaries =
                workoutDiaryPort.findDiariesByUserIdAndDate(
                        targetUserId,
                        date
                );

        log.info("event=calendarDay_find_succeed userId={}", targetUserId);

        return new CalendarDayResult(
                date,
                pts,
                diaries
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarMonthResult findCalendarMonth(
            Long requesterUserId,
            Long targetUserId,
            Integer year,
            Integer month
    ) {

        validateMonthRequest(targetUserId, year, month);
        validateCalendarAccess(requesterUserId, targetUserId);

        log.info(
                "event=calendar_month_find_start userId={} year={} month={}",
                targetUserId,
                year,
                month
        );

        return calendarMonthReader.findCalendarMonth(
                targetUserId,
                year,
                month
        );
    }

    private void validateMonthRequest(
            Long userId,
            Integer year,
            Integer month
    ) {
        if (userId == null) {
            throw new CalendarException(CalendarErrorCode.USER_ID_REQUIRED);
        }

        if (year == null) {
            throw new CalendarException(CalendarErrorCode.YEAR_REQUIRED);
        }

        if (month == null) {
            throw new CalendarException(CalendarErrorCode.MONTH_REQUIRED);
        }

        if (month < 1 || month > 12) {
            throw new CalendarException(CalendarErrorCode.INVALID_MONTH);
        }
    }

    private void validateCalendarAccess(
            Long requesterUserId,
            Long targetUserId
    ) {
        if (requesterUserId == null || targetUserId == null) {
            throw new CalendarException(CalendarErrorCode.USER_ID_REQUIRED);
        }

        if (requesterUserId.equals(targetUserId)) {
            return;
        }

        boolean accessible = calendarPtReservationPort.existsActivePtRelationWithTrainer(
                targetUserId,
                requesterUserId
        );

        if (!accessible) {
            throw new CalendarException(CalendarErrorCode.CALENDAR_ACCESS_DENIED);
        }
    }
}
