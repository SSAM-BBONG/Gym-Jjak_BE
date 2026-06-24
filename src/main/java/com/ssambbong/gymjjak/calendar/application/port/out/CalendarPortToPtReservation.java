package com.ssambbong.gymjjak.calendar.application.port.out;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayPtResult;

import java.time.LocalDate;
import java.util.List;

public interface CalendarPortToPtReservation {

    List<CalendarDayPtResult> findPtsByUserIdAndDate(
            Long userId,
            LocalDate date
    );
}
