package com.ssambbong.gymjjak.ptReservation.application.command;

import java.time.LocalDateTime;

public record CreatePtReservationCommand(
        Long userId,
        Long ptCourseId,
        LocalDateTime reservedStartAt,
        LocalDateTime reservedEndAt
) {
}
