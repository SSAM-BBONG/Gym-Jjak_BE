package com.ssambbong.gymjjak.calendar.application.result;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayDiaryResult(
        Long workoutDiaryId,
        Long exerciseId,
        String exercise,
        LocalDate date,
        PartType part,
        List<CalendarDayDiarySetResult> sets
) {
}
