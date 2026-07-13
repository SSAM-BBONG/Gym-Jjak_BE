package com.ssambbong.gymjjak.calendar.application.service;

import com.ssambbong.gymjjak.calendar.application.port.in.CalendarUsecase;
import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPortToPtReservation;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayPtResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthResult;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService implements CalendarUsecase {

    private final CalendarPortToPtReservation calendarPortToPtReservation;
    private final WorkoutDiaryPort workoutDiaryPort;
    private final CalendarMonthReader calendarMonthReader;

    @Override
    @Transactional(readOnly = true)
    public CalendarDayResult findCalendarDay(
            Long userId,
            LocalDate date
    ) {
        if (userId == null) {
            throw new CalendarException(CalendarErrorCode.USER_ID_REQUIRED);
        }

        if (date == null) {
            throw new CalendarException(CalendarErrorCode.DATE_REQUIRED);
        }

        log.debug("event=calendarDay_find_start userId={}", userId);
        List<CalendarDayPtResult> pts =
                calendarPortToPtReservation.findPtsByUserIdAndDate(
                        userId,
                        date
                );

        List<CalendarDayDiaryResult> diaries =
                workoutDiaryPort.findDiariesByUserIdAndDate(
                        userId,
                        date
                );

        log.info("event=calendarDay_find_succeed userId={}", userId);

        return new CalendarDayResult(
                date,
                pts,
                diaries
        );
    }

    @Cacheable(
            cacheNames = "calendarMonth",
            key = "'user:' + #userId + ':year:' + #year + ':month:' + #month",
            sync = true
    )
    @Override
    public CalendarMonthResult findCalendarMonth(
            Long userId,
            Integer year,
            Integer month
    ) {

        validateMonthRequest(userId, year, month);

        log.info(
                "event=calendar_month_cache_miss userId={} year={} month={}",
                userId,
                year,
                month
        );

        return calendarMonthReader.findCalendarMonth(
                userId,
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
}
