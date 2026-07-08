package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.MonthlyPtReservationResult;
import lombok.Builder;

import java.util.List;

// 어드민 대시보드 월별 예약된 pt 수 response 클래스
@Builder
public record AdminMonthlyPtReservationStatisticsResponse(
        List<MonthlyPtReservationResponse> monthlyReservations
) {

    public static AdminMonthlyPtReservationStatisticsResponse from(
            List<MonthlyPtReservationResult> results
    ) {
        return AdminMonthlyPtReservationStatisticsResponse.builder()
                .monthlyReservations(
                        results.stream()
                                .map(MonthlyPtReservationResponse::from)
                                .toList()
                )
                .build();
    }
}
