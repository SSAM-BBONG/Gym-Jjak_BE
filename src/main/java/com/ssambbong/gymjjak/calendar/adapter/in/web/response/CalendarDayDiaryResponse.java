package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayDiaryResponse(
        Long workoutDiaryId,
        String exercise,
        LocalDate date,
        PartType part,
        String partName,
        List<CalendarDayDiarySetResponse> sets
) {
    public static CalendarDayDiaryResponse from(CalendarDayDiaryResult result) {
        return new CalendarDayDiaryResponse(
                result.workoutDiaryId(),
                result.exercise(),
                result.date(),
                result.part(),
                toPartName(result.part()),
                result.sets()
                        .stream()
                        .map(CalendarDayDiarySetResponse::from)
                        .toList()
        );
    }

    private static String toPartName(PartType part) {
        return switch (part) {
            case CHEST -> "가슴";
            case BACK -> "등";
            case SHOULDER -> "어깨";
            case ARM -> "팔";
            case ABS -> "복근";
            case CORE -> "코어";
            case LEG -> "하체";
            case GLUTE -> "둔근";
            case FULL_BODY -> "전신";
        };
    }
}
