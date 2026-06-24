package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayPtResult;

import java.time.LocalDate;

public record CalendarDayPtResponse(
        LocalDate date,
        String title,
        Long ptId
) {

    public static CalendarDayPtResponse from(CalendarDayPtResult result) {
        return new CalendarDayPtResponse(
                result.date(),
                result.title(),
                result.ptId()
        );
    }
}
