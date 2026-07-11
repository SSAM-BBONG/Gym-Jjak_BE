package com.ssambbong.gymjjak.calendar.application.command;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.time.LocalDate;
import java.util.List;

public record CreateWorkoutDiaryCommand(
        LocalDate diaryDate,
        PartType part,
        String exercise,
        List<WorkoutDiarySetCommand> sets
) {
}
