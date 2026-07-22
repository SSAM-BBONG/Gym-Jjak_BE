package com.ssambbong.gymjjak.dashboard.admin.application.port;

import java.time.LocalDate;

// payments 테이블에서 조회한 월별 원본 결제 금액
public record MonthlyPaymentRevenue(
        LocalDate month,
        long ptPaymentAmount,
        long subscriptionPaymentAmount
) {
}
