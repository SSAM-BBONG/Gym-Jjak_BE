package com.ssambbong.gymjjak.pt.ptReservation.application.port;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtReservationCalendarResult;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface FindPtReservationCalendarPort {
    List<PtReservationCalendarResult> findCalendarByUserIdAndMonth(
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            PtReservationStatus cancelledStatus
    );
}
