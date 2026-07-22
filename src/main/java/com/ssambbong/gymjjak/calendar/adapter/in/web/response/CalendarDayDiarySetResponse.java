package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiarySetResult;

import java.math.BigDecimal;

public record CalendarDayDiarySetResponse(
        Long setId,
        Integer setOrder,
        BigDecimal weight,
        Integer reps
) {
    public static CalendarDayDiarySetResponse from(CalendarDayDiarySetResult result) {
        return new CalendarDayDiarySetResponse(
                result.setId(),
                result.setOrder(),
                result.weight(),
                result.reps()
        );
    }
}
