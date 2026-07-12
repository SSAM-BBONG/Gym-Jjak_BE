package com.ssambbong.gymjjak.calendar.application.result;

import java.time.LocalDate;

public record CalendarMonthDiaryResult(
        LocalDate date,
        Long workoutDiaryId,
        String exercise
) {
}
