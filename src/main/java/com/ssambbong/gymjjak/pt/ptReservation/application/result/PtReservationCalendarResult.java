package com.ssambbong.gymjjak.pt.ptReservation.application.result;

import java.time.LocalDateTime;

public record PtReservationCalendarResult(
        LocalDateTime reservedStartAt,
        String ptTitle
) {
}
