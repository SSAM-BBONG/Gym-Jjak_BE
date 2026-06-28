package com.ssambbong.gymjjak.calendar.application.result;

import java.util.List;

public record CalendarMonthResult(
        int year,
        int month,
        List<CalendarMonthDayResult> days
) {
}
