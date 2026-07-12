package com.ssambbong.gymjjak.calendar.application.service;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPortToPtReservation;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDayResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthPtResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CalendarMonthReader {

    private final CalendarPortToPtReservation calendarPortToPtReservation;
    private final WorkoutDiaryPort workoutDiaryPort;

    public CalendarMonthResult findCalendarMonth(
            Long userId,
            Integer year,
            Integer month
    ) {
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
                workoutDiaryPort.findDiarySummariesByUserIdAndPeriod(
                        userId,
                        startDate,
                        endDate
                );

        Map<LocalDate, CalendarMonthDayAccumulator> dayMap = new TreeMap<>();

        for (CalendarMonthPtResult pt : pts) {
            dayMap.computeIfAbsent(
                    pt.date(),
                    CalendarMonthDayAccumulator::new
            ).markPt();
        }

        for (CalendarMonthDiaryResult diary : diaries) {
            dayMap.computeIfAbsent(
                    diary.date(),
                    CalendarMonthDayAccumulator::new
            ).addDiary(diary.exercise());
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
        private String representativeExercise;
        private int diaryCount;

        private CalendarMonthDayAccumulator(LocalDate date) {
            this.date = date;
        }

        private void markPt() {
            this.hasPt = true;
        }

        private void addDiary(String exercise) {
            if (diaryCount == 0) {
                this.representativeExercise = exercise;
            }
            this.diaryCount++;
        }

        private CalendarMonthDayResult toResult() {
            return new CalendarMonthDayResult(
                    date,
                    hasPt,
                    diarySummary()
            );
        }

        private String diarySummary() {
            if (diaryCount == 0) {
                return null;
            }
            if (diaryCount == 1) {
                return representativeExercise;
            }
            return representativeExercise + " 외 " + (diaryCount - 1) + "개";
        }
    }
}
