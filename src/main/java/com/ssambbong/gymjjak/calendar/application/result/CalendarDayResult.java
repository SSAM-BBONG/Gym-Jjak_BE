package com.ssambbong.gymjjak.calendar.application.result;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayResult(
        LocalDate date,
        List<CalendarDayPtResult> pts,
        CalendarDayDiaryResult diary
) {
}
