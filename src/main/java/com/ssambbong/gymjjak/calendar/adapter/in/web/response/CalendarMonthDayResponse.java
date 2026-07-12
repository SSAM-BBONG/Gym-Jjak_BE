package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDayResult;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CalendarMonthDayResponse(
        LocalDate date,
        Boolean pt,
        String diarySummary
) {
    public static CalendarMonthDayResponse from(CalendarMonthDayResult result) {
        return new CalendarMonthDayResponse(
                result.date(),
                result.hasPt() ? Boolean.TRUE : null,
                result.diarySummary()
        );
    }
}
