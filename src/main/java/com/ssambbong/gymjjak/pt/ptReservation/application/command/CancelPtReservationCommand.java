package com.ssambbong.gymjjak.pt.ptReservation.application.command;

public record CancelPtReservationCommand(
        Long userId,
        Long ptReservationId
) {
}
