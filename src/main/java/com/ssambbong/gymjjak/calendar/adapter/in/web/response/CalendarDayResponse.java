package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayResult;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayResponse(
        LocalDate date,
        List<CalendarDayPtResponse> pts,
        List<CalendarDayDiaryResponse> diaries
) {

    public static CalendarDayResponse from(CalendarDayResult result) {
        return new CalendarDayResponse(
                result.date(),
                result.pts().stream()
                        .map(CalendarDayPtResponse::from)
                        .toList(),
                result.diaries().stream()
                        .map(CalendarDayDiaryResponse::from)
                        .toList()
        );
    }
}
