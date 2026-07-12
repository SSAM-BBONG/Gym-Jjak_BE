package com.ssambbong.gymjjak.calendar.application.result;

import java.math.BigDecimal;

public record CalendarDayDiarySetResult(
        Long setId,
        Integer setOrder,
        BigDecimal weight,
        Integer reps
) {
}
