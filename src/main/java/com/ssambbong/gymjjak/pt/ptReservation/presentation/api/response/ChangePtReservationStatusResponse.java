package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

public record ChangePtReservationStatusResponse(
        PtReservationStatus status,
        int progressCount,
        int totalSessionCount
) {
}
