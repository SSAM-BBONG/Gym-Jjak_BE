package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;

import java.time.LocalDate;

public record CalendarDayDiaryResponse(
        String title,
        String content,
        LocalDate date,
        String category
) {
    public static CalendarDayDiaryResponse from(CalendarDayDiaryResult result) {
        return new CalendarDayDiaryResponse(
                result.title(),
                result.content(),
                result.date(),
                result.category()
        );
    }
}
