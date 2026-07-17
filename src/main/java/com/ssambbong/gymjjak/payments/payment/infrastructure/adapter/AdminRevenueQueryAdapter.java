package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.dashboard.admin.application.port.AdminRevenueQueryPort;
import com.ssambbong.gymjjak.dashboard.admin.application.port.MonthlyPaymentRevenue;
import com.ssambbong.gymjjak.payments.payment.infrastructure.persistence.SpringDataPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminRevenueQueryAdapter implements AdminRevenueQueryPort {

    private final SpringDataPaymentRepository repository;

    // adminDashboard <-> payment Port 구현
    @Override
    public List<MonthlyPaymentRevenue> findMonthlyPaymentRevenue(
            LocalDateTime startDate, LocalDateTime endDate
    ) {
        return repository.findAdminMonthlyPaymentRevenues(startDate, endDate)
                .stream()
                .map(row -> new MonthlyPaymentRevenue(
                        row.getMonth(),
                        row.getPtPaymentAmount() == null ? 0L : row.getPtPaymentAmount(),
                        row.getSubscriptionPaymentAmount() == null ? 0L : row.getSubscriptionPaymentAmount()
                ))
                .toList();
    }
}
