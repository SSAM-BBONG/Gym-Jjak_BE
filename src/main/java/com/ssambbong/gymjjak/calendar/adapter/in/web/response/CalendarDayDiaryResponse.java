package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.global.presentation.api.common.PartTypeNameMapper;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayDiaryResponse(
        Long workoutDiaryId,
        String exercise,
        LocalDate date,
        String part,
        List<CalendarDayDiarySetResponse> sets
) {
    public static CalendarDayDiaryResponse from(CalendarDayDiaryResult result) {
        return new CalendarDayDiaryResponse(
                result.workoutDiaryId(),
                result.exercise(),
                result.date(),
                PartTypeNameMapper.toKoreanName(result.part()),
                result.sets()
                        .stream()
                        .map(CalendarDayDiarySetResponse::from)
                        .toList()
        );
    }
}
