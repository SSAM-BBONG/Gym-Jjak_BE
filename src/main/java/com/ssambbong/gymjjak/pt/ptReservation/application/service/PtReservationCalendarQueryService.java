package com.ssambbong.gymjjak.pt.ptReservation.application.service;

import com.ssambbong.gymjjak.pt.ptReservation.application.port.FindPtReservationCalendarPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtReservationCalendarResult;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.FindPtReservationCalendarUsecase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PtReservationCalendarQueryService implements FindPtReservationCalendarUsecase {

    private final FindPtReservationCalendarPort findPtReservationCalendarPort;

    @Override
    public List<PtReservationCalendarResult> findPtReservationCalendar(
            Long userId,
            int year,
            int month
    ) {
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDateTime startAt = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endAt = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        return findPtReservationCalendarPort.findCalendarByUserIdAndMonth(
                userId,
                startAt,
                endAt,
                PtReservationStatus.CANCELLED
        );
    }
}
