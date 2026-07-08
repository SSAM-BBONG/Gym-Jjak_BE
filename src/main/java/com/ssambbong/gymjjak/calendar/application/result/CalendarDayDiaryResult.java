package com.ssambbong.gymjjak.calendar.application.result;

import java.time.LocalDate;

public record CalendarDayDiaryResult(
        Long workoutDiaryId,
        String title,
        String content,
        LocalDate date
) {
}
