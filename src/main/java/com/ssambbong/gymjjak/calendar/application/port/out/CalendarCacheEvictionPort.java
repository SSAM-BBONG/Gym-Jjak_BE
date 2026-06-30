package com.ssambbong.gymjjak.calendar.application.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

public interface CalendarCacheEvictionPort {

    void evictMonth(Long userId, LocalDate date);

    void evictMonth(Long userId, LocalDateTime dateTime);

    void evictMonths(Long userId, LocalDate oldDate, LocalDate newDate);

    void evictMonths(Long userId, LocalDateTime oldDateTime, LocalDateTime newDateTime);
}
