package com.ssambbong.gymjjak.calendar.application.result;

import java.time.LocalDate;

public record CalendarMonthDayResult(
        LocalDate date,
        boolean hasPt,
        String diaryTitle
) {
}
