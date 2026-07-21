package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtSessionStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record CancelPtReservationResponse(
        PtSessionStatus sessionStatus,
        LocalDateTime cancelledAt
) {
    public static CancelPtReservationResponse cancelled() {
        return new CancelPtReservationResponse(
                PtSessionStatus.CANCELLED,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    // sessionStatus를 파라미터로 받아서 그대로 넣어주는 of 메서드
    public static CancelPtReservationResponse of(PtSessionStatus sessionStatus) {
        return new CancelPtReservationResponse(
                sessionStatus,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }
}
