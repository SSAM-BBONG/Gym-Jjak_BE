package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtReservationCalendarResult;

import java.time.LocalDate;

public record PtReservationCalendarItemResponse(
        LocalDate date,
        String ptTitle
) {
    public static PtReservationCalendarItemResponse from(PtReservationCalendarResult result) {
        return new PtReservationCalendarItemResponse(
                result.reservedStartAt().toLocalDate(),
                result.ptTitle()
        );
    }
}
