package com.ssambbong.gymjjak.global.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
public class CalendarCacheEvictor {

    private static final String CALENDAR_MONTH_CACHE = "calendarMonth";

    private final CacheManager cacheManager;

    public void evictMonth(Long userId, LocalDate date) {
        String key = CalendarCacheKeys.month(userId, date);

        Cache cache = cacheManager.getCache(CALENDAR_MONTH_CACHE);

        if (cache != null) {
            cache.evict(key);
        }
    }

    public void evictMonth(Long userId, LocalDateTime dateTime) {
        evictMonth(userId, dateTime.toLocalDate());
    }

    public void evictMonths(Long userId, LocalDate oldDate, LocalDate newDate) {
        evictMonth(userId, oldDate);

        YearMonth oldMonth = YearMonth.from(oldDate);
        YearMonth newMonth = YearMonth.from(newDate);

        if (!oldMonth.equals(newMonth)) {
            evictMonth(userId, newDate);
        }
    }

    public void evictMonths(Long userId, LocalDateTime oldDateTime, LocalDateTime newDateTime) {
        evictMonths(
                userId,
                oldDateTime.toLocalDate(),
                newDateTime.toLocalDate()
        );
    }
}
