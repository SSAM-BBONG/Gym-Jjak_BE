package com.ssambbong.gymjjak.calendar.application.port.out;

import com.ssambbong.gymjjak.calendar.application.result.CalendarDayPtResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthPtResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CalendarPtReservationPort {

    List<CalendarDayPtResult> findPtsByUserIdAndDate(
            Long userId,
            LocalDate date
    );

    List<CalendarMonthPtResult> findPtDatesByUserIdAndPeriod(
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt
    );

    boolean existsActivePtRelationWithTrainer(
            Long targetUserId,
            Long trainerUserId
    );
}
