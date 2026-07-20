package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtSessionStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record CancelPtReservationResponse(
        PtSessionStatus sessionStatus,
        LocalDateTime cancelledAt
) {
    public static CancelPtReservationResponse cancelled() {
        return new CancelPtReservationResponse(
                PtSessionStatus.CANCELLED,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }
}
