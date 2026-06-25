package com.ssambbong.gymjjak.calendar.application.result;

import java.time.LocalDate;

public record CalendarDayDiaryResult(
        String title,
        String content,
        LocalDate date,
        String category
) {
}
