package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthResult;

import java.util.List;

public record CalendarMonthResponse(
        int year,
        int month,
        List<CalendarMonthDayResponse> days
) {
    public static CalendarMonthResponse from(CalendarMonthResult result) {
        return new CalendarMonthResponse(
                result.year(),
                result.month(),
                result.days()
                        .stream()
                        .map(CalendarMonthDayResponse::from)
                        .toList()
        );
    }
}
