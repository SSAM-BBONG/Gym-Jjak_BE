package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.MonthlyPtReservationResult;
import lombok.Builder;

// 어드민 대시보드 각 월 예약된 pt 수 response 클래스
@Builder
public record MonthlyPtReservationResponse(
        String month,
        long count
) {
    public static MonthlyPtReservationResponse from(
            MonthlyPtReservationResult result
    ) {
        return MonthlyPtReservationResponse.builder()
                .month(result.month())
                .count(result.count())
                .build();
    }
}
