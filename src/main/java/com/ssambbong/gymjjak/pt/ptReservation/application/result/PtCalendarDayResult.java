package com.ssambbong.gymjjak.pt.ptReservation.application.result;

import java.time.LocalDateTime;

public record PtCalendarDayResult(
        Long ptCourseId,
        String ptTitle,
        LocalDateTime reservedStartAt
) {
}
