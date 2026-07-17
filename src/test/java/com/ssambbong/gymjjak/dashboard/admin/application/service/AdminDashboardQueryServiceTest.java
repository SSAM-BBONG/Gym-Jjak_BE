package com.ssambbong.gymjjak.dashboard.admin.application.service;

import com.ssambbong.gymjjak.dashboard.admin.application.port.AdminRevenueQueryPort;
import com.ssambbong.gymjjak.dashboard.admin.application.port.MonthlyPaymentRevenue;
import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminRevenueStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.query.MonthlyRevenueResult;
import com.ssambbong.gymjjak.organization.organization.infrastructure.persistence.SpringDataOrganizationRepository;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import com.ssambbong.gymjjak.report.infrastructure.persistence.SpringDataReportGroupRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardQueryServiceTest {

    private static final Clock TEST_CLOCK = Clock.fixed(
            Instant.parse("2026-07-15T00:00:00Z"),
            ZoneId.of("Asia/Seoul")
    );

    @Mock
    private AdminRevenueQueryPort adminRevenueQueryPort;

    @Mock
    private SpringDataUserRepository userRepository;

    @Mock
    private SpringDataTrainerProfileRepository trainerProfileRepository;

    @Mock
    private SpringDataOrganizationRepository organizationRepository;

    @Mock
    private SpringDataPtCourseRepository ptCourseRepository;

    @Mock
    private SpringDataReportGroupRepository reportGroupRepository;

    private AdminDashboardQueryService adminDashboardQueryService;

    @BeforeEach
    void setUp() {
        adminDashboardQueryService = new AdminDashboardQueryService(
                adminRevenueQueryPort,
                userRepository,
                trainerProfileRepository,
                organizationRepository,
                ptCourseRepository,
                reportGroupRepository,
                TEST_CLOCK
        );
    }

    @Test
    void findRevenueStatistics_returnsRecentSixMonthsAndFillsMissingMonthsWithZero() {
        when(adminRevenueQueryPort.findMonthlyPaymentRevenue(
                LocalDateTime.of(2026, 2, 1, 0, 0),
                LocalDateTime.of(2026, 8, 1, 0, 0)
        )).thenReturn(List.of(
                new MonthlyPaymentRevenue(
                        LocalDate.of(2026, 6, 1),
                        100_000L,
                        50_000L
                )
        ));

        AdminRevenueStatisticsResult result =
                adminDashboardQueryService.findRevenueStatistics();

        List<MonthlyRevenueResult> monthlyRevenues = result.monthlyRevenues();

        assertThat(monthlyRevenues)
                .extracting(MonthlyRevenueResult::month)
                .containsExactly(
                        "2026-02", "2026-03", "2026-04",
                        "2026-05", "2026-06", "2026-07"
                );

        assertThat(monthlyRevenues.get(0))
                .extracting(
                        MonthlyRevenueResult::ptCommissionRevenue,
                        MonthlyRevenueResult::subscriptionRevenue,
                        MonthlyRevenueResult::totalRevenue
                )
                .containsExactly(0L, 0L, 0L);

        assertThat(monthlyRevenues.get(4))
                .extracting(
                        MonthlyRevenueResult::ptCommissionRevenue,
                        MonthlyRevenueResult::subscriptionRevenue,
                        MonthlyRevenueResult::totalRevenue
                )
                .containsExactly(10_000L, 50_000L, 60_000L);

        verify(adminRevenueQueryPort).findMonthlyPaymentRevenue(
                LocalDateTime.of(2026, 2, 1, 0, 0),
                LocalDateTime.of(2026, 8, 1, 0, 0)
        );
    }

    @Test
    void findRevenueStatistics_roundsMonthlyPtCommissionWithHalfUp() {
        when(adminRevenueQueryPort.findMonthlyPaymentRevenue(
                LocalDateTime.of(2026, 2, 1, 0, 0),
                LocalDateTime.of(2026, 8, 1, 0, 0)
        )).thenReturn(List.of(
                new MonthlyPaymentRevenue(
                        LocalDate.of(2026, 7, 1),
                        12_345L,
                        10_000L
                )
        ));

        AdminRevenueStatisticsResult result =
                adminDashboardQueryService.findRevenueStatistics();

        MonthlyRevenueResult julyRevenue = result.monthlyRevenues().get(5);

        assertThat(julyRevenue.ptCommissionRevenue()).isEqualTo(1_235L);
        assertThat(julyRevenue.subscriptionRevenue()).isEqualTo(10_000L);
        assertThat(julyRevenue.totalRevenue()).isEqualTo(11_235L);
    }
}
