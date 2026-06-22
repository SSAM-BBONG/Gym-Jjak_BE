package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.request;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import jakarta.validation.constraints.NotNull;

public record ChangePtReservationStatusRequest(
        @NotNull PtReservationStatus status
        ) {
}
