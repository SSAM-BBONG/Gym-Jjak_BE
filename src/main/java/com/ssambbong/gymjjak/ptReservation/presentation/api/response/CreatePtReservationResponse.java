package com.ssambbong.gymjjak.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.ptReservation.domain.model.PtReservationStatus;

public record CreatePtReservationResponse(
        Long reservationId,
        PtReservationStatus status
) {
}
