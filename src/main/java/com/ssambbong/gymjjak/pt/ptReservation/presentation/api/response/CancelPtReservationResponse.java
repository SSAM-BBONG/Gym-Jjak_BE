package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record CancelPtReservationResponse(
        PtReservationStatus status,
        LocalDateTime cancelledAt
) {
    public static CancelPtReservationResponse cancelled() {
        return new CancelPtReservationResponse(
                PtReservationStatus.CANCELLED,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }
}
