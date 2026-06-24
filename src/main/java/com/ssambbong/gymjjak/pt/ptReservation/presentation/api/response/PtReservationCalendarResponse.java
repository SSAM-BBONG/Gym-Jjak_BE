package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtReservationCalendarResult;

import java.util.List;

public record PtReservationCalendarResponse(
        int year,
        int month,
        List<PtReservationCalendarItemResponse> pts
) {

    public static PtReservationCalendarResponse of(
            int year,
            int month,
            List<PtReservationCalendarResult> results
    ) {
        return new PtReservationCalendarResponse(
                year,
                month,
                results.stream()
                        .map(PtReservationCalendarItemResponse::from)
                        .toList()
        );
    }
}
