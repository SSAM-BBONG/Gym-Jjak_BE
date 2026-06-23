package com.ssambbong.gymjjak.calendar.application.command;

public record UpdateWorkoutDiaryCommand(
        String categoryName,
        String title,
        String content
) {
}
