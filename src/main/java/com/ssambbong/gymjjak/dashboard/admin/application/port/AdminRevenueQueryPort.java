package com.ssambbong.gymjjak.dashboard.admin.application.port;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminRevenueQueryPort {

    // 기간 내 월별 PT, 구독권 결제 금액 조회
    List<MonthlyPaymentRevenue> findMonthlyPaymentRevenue(
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
