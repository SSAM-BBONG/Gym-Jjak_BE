package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDateTime;

public record CancelPtReservationResponse(
        PtReservationStatus status,
        LocalDateTime cancelledAt
) {
    // 도메인 객체로부터 응답 생성
    public static CancelPtReservationResponse from(PtReservation reservation) {
        return new CancelPtReservationResponse(
                reservation.getStatus(),
                reservation.getCancelledAt()
        );
    }
}
