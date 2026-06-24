package com.ssambbong.gymjjak.calendar.application.service;

import com.ssambbong.gymjjak.calendar.application.port.in.CalendarUsecase;
import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPortToPtReservation;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayPtResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

        return new CalendarDayResult(
                date,
                pts,
                diary
        );
    }
}
