package com.ssambbong.gymjjak.calendar.application.result;

import java.time.LocalDate;

public record CalendarDayPtResult(
        LocalDate date,
        String title,
        Long ptId
) {
}
