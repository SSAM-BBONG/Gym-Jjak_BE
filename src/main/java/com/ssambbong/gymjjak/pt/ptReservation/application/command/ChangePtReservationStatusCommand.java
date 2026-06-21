package com.ssambbong.gymjjak.pt.ptReservation.application.command;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

public record ChangePtReservationStatusCommand(
        Long userId,
        Long ptReservationId,
        PtReservationStatus status
) {
}
