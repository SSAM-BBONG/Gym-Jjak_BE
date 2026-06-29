package com.ssambbong.gymjjak.global.infrastructure.cache;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class CalendarCacheKeys {

    private CalendarCacheKeys() {
    }

    public static String month(Long userId, int year, int month) {
        return "user:%d:year:%d:month:%d".formatted(
                userId,
                year,
                month
        );
    }

    public static String month(Long userId, LocalDate date) {
        return month(
                userId,
                date.getYear(),
                date.getMonthValue()
        );
    }

    public static String month(Long userId, LocalDateTime dateTime) {
        return month(
                userId,
                dateTime.toLocalDate()
        );
    }
}
