package com.ssambbong.gymjjak.calendar.application.service;

import com.ssambbong.gymjjak.calendar.application.port.in.CalendarUsecase;
import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPortToPtReservation;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.*;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService implements CalendarUsecase {

    private final CalendarPortToPtReservation calendarPortToPtReservation;
    private final WorkoutDiaryPort workoutDiaryPort;

    @Override
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

        CalendarDayDiaryResult diary =
                workoutDiaryPort.findDiaryByUserIdAndDate(
                        userId,
                        date
                ).orElse(null);

        log.info("event=calendarDay_find_succeed userId={}", userId);

        return new CalendarDayResult(
                date,
                pts,
                diary
        );
    }

    @Cacheable(
            cacheNames = "calendarMonth",
            key = "'user:' + #userId + ':year:' + #year + ':month:' + #month"
    )
    @Override
    public CalendarMonthResult findCalendarMonth(
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

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.plusMonths(1).atDay(1);

        LocalDateTime startAt = startDate.atStartOfDay();
        LocalDateTime endAt = endDate.atStartOfDay();

        log.debug("event=calendarMonth_find_start userId={}", userId);

        List<CalendarMonthPtResult> pts =
                calendarPortToPtReservation.findPtDatesByUserIdAndPeriod(
                        userId,
                        startAt,
                        endAt
                );

        List<CalendarMonthDiaryResult> diaries =
                workoutDiaryPort.findDiaryTitlesByUserIdAndPeriod(
                        userId,
                        startDate,
                        endDate
                );

        Map<LocalDate, CalendarMonthDayAccumulator> dayMap = new TreeMap<>();

        for (CalendarMonthPtResult pt : pts) {
            dayMap.computeIfAbsent(
                    pt.date(),
                    date -> new CalendarMonthDayAccumulator(date)
            ).markPt();
        }

        for (CalendarMonthDiaryResult diary : diaries) {
            dayMap.computeIfAbsent(
                    diary.date(),
                    date -> new CalendarMonthDayAccumulator(date)
            ).setDiaryTitle(diary.diaryTitle());
        }

        List<CalendarMonthDayResult> days = dayMap.values()
                .stream()
                .map(CalendarMonthDayAccumulator::toResult)
                .toList();

        log.info("event=calendarMonth_find_succeed userId={}", userId);

        return new CalendarMonthResult(
                year,
                month,
                days
        );
    }

    private static class CalendarMonthDayAccumulator {

        private final LocalDate date;
        private boolean hasPt;
        private String diaryTitle;

        private CalendarMonthDayAccumulator(LocalDate date) {
            this.date = date;
        }

        private void markPt() {
            this.hasPt = true;
        }

        private void setDiaryTitle(String diaryTitle) {
            this.diaryTitle = diaryTitle;
        }

        private CalendarMonthDayResult toResult() {
            return new CalendarMonthDayResult(
                    date,
                    hasPt,
                    diaryTitle
            );
        }
    }
}
