package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.dashboard.admin.application.port.MonthlyPaymentRevenue;
import com.ssambbong.gymjjak.payments.payment.infrastructure.persistence.SpringDataPaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminRevenueQueryAdapterTest {

    @Mock
    private SpringDataPaymentRepository repository;

    @Mock
    private SpringDataPaymentRepository.AdminMonthlyRevenueRow monthlyRevenueRow;

    @InjectMocks
    private AdminRevenueQueryAdapter adminRevenueQueryAdapter;

    @Test
    void findMonthlyPaymentRevenue_mapsProjectionToMonthlyPaymentRevenue() {
        LocalDateTime startDate = LocalDateTime.of(2026, 2, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 8, 1, 0, 0);

        when(monthlyRevenueRow.getMonth()).thenReturn(LocalDate.of(2026, 7, 1));
        when(monthlyRevenueRow.getPtPaymentAmount()).thenReturn(300_000L);
        when(monthlyRevenueRow.getSubscriptionPaymentAmount()).thenReturn(120_000L);
        when(repository.findAdminMonthlyPaymentRevenues(startDate, endDate))
                .thenReturn(List.of(monthlyRevenueRow));

        List<MonthlyPaymentRevenue> result =
                adminRevenueQueryAdapter.findMonthlyPaymentRevenue(startDate, endDate);

        assertThat(result).containsExactly(
                new MonthlyPaymentRevenue(
                        LocalDate.of(2026, 7, 1),
                        300_000L,
                        120_000L
                )
        );

        verify(repository).findAdminMonthlyPaymentRevenues(startDate, endDate);
    }
}
