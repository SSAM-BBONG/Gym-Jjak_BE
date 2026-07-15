package com.ssambbong.gymjjak.calendar.application.port.in;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthResult;

import java.time.LocalDate;

public interface CalendarUsecase {

    CalendarDayResult findCalendarDay(
            Long requesterUserId,
            Long targetUserId,
            LocalDate date
    );

    CalendarMonthResult findCalendarMonth(
            Long requesterUserId,
            Long targetUserId,
            Integer year,
            Integer month
    );
}
