package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPtReservationPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayPtResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthPtResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CalendarPtReservationPersistenceAdapter implements CalendarPtReservationPort {

    private final CalendarPtReservationJpaRepository calendarPtReservationJpaRepository;

    @Override
    public List<CalendarDayPtResult> findPtsByUserIdAndDate(
            Long userId,
            LocalDate date
    ) {
        LocalDateTime startAt = date.atStartOfDay();
        LocalDateTime endAt = date.plusDays(1).atStartOfDay();

        return calendarPtReservationJpaRepository.findCalendarDayPtsByUserIdAndDate(
                        userId,
                        startAt,
                        endAt
                )
                .stream()
                .map(this::toCalendarDayPtResult)
                .toList();
    }

    @Override
    public List<CalendarMonthPtResult> findPtDatesByUserIdAndPeriod(
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        return calendarPtReservationJpaRepository.findReservedStartAtsByUserIdAndPeriod(
                        userId,
                        startAt,
                        endAt
                )
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .map(CalendarMonthPtResult::new)
                .toList();
    }

    @Override
    public boolean existsActivePtRelationWithTrainer(
            Long targetUserId,
            Long trainerUserId
    ) {
        return calendarPtReservationJpaRepository.existsActivePtRelationWithTrainer(
                targetUserId,
                trainerUserId
        );
    }

    private CalendarDayPtResult toCalendarDayPtResult(
            CalendarPtReservationJpaRepository.CalendarDayPtRow row
    ) {
        return new CalendarDayPtResult(
                row.getReservedStartAt().toLocalDate(),
                row.getTitle(),
                row.getPtId()
        );
    }
}
