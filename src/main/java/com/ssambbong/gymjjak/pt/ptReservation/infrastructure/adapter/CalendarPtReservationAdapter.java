package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPortToPtReservation;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayPtResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthPtResult;
import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtCalendarDayResult;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CalendarPtReservationAdapter implements CalendarPortToPtReservation {

    private final SpringDataPtReservationRepository springDataPtReservationRepository;

    @Override
    public List<CalendarDayPtResult> findPtsByUserIdAndDate(
            Long userId,
            LocalDate date
    ) {
        LocalDateTime startAt = date.atStartOfDay();
        LocalDateTime endAt = date.plusDays(1).atStartOfDay();

        return springDataPtReservationRepository.findCalendarDayPtsByUserIdAndDate(
                        userId,
                        startAt,
                        endAt,
                        PtReservationStatus.CANCELLED
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
        return springDataPtReservationRepository.findReservedStartAtsByUserIdAndPeriod(
                        userId,
                        startAt,
                        endAt,
                        PtReservationStatus.CANCELLED
                )
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .map(CalendarMonthPtResult::new)
                .toList();
    }

    private CalendarDayPtResult toCalendarDayPtResult(
            PtCalendarDayResult result
    ) {
        return new CalendarDayPtResult(
                result.reservedStartAt().toLocalDate(),
                result.ptTitle(),
                result.ptCourseId()
        );
    }


}
