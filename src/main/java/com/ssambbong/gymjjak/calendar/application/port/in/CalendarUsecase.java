package com.ssambbong.gymjjak.calendar.application.port.in;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayResult;

import java.time.LocalDate;

public interface CalendarUsecase {

    CalendarDayResult findCalendarDay(
            Long userId,
            LocalDate date
    );
}
