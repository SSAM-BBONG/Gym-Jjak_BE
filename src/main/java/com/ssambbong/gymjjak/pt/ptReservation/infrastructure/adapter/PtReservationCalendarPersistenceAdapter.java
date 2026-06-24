package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptReservation.application.port.FindPtReservationCalendarPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtReservationCalendarResult;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PtReservationCalendarPersistenceAdapter implements FindPtReservationCalendarPort {

    private final SpringDataPtReservationRepository springDataPtReservationRepository;

    @Override
    public List<PtReservationCalendarResult> findCalendarByUserIdAndMonth(
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            PtReservationStatus cancelledStatus
    ) {
        return springDataPtReservationRepository.findCalendarByUserIdAndMonth(
                userId,
                startAt,
                endAt,
                cancelledStatus
        );
    }
}
