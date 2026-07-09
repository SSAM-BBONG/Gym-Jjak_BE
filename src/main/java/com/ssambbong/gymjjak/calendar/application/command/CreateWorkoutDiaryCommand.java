package com.ssambbong.gymjjak.calendar.application.command;

import java.time.LocalDate;

public record CreateWorkoutDiaryCommand(
        LocalDate diaryDate,
        String title,
        String content
) {
}
