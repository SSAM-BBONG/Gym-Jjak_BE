package com.ssambbong.gymjjak.pt.ptReservation.application.result;

import lombok.Builder;

// adminDashBoard에서 사용할 월별 예약된 pt 수 result 클래스
@Builder
public record MonthlyPtReservationResult(
        String month,
        long count
) {
}
