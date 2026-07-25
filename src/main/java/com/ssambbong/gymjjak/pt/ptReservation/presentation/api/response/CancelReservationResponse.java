package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record CancelReservationResponse(
        PtReservationStatus status,
        LocalDateTime cancelledAt
) {
    public static CancelReservationResponse cancelled() {
        return new CancelReservationResponse(
                PtReservationStatus.CANCELLED,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }
}
