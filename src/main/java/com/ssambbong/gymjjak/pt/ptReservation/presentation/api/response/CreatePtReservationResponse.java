package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

public record CreatePtReservationResponse(
        Long reservationId,
        PtReservationStatus status
) {
}
