package com.ssambbong.gymjjak.organization;

import com.ssambbong.gymjjak.organization.application.service.OrganizationApplicationQueryService;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrganizationApplicationQueryServiceTest {

    private OrganizationApplicationRepository organizationApplicationRepository;
    private OrganizationApplicationQueryService organizationApplicationQueryService;

    @BeforeEach
    void setUp() {
        organizationApplicationRepository = mock(OrganizationApplicationRepository.class);
        organizationApplicationQueryService = new OrganizationApplicationQueryService(organizationApplicationRepository);
    }

    @Test
    @DisplayName("내 신청 목록 조회에 성공한다")
    void findMyOrganizationApplications_success() {
        // given
        Long applicantUserId = 1L;

        OrganizationApplication application = OrganizationApplication.restore(
                1L,
                applicantUserId,
                "gymjjak123",
                1L,
                "1234567890",
                "짐짝헬스장",
                "홍길동",
                "010-1234-5678",
                LocalDate.of(2024, 1, 1),
                "서울시 강남구 테헤란로 1",
                null,
                null,
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276"),
                null,
                null,
                null,
                "02-1234-5678",
                OrganizationApplicationStatus.PENDING,
                null,
                LocalDateTime.of(2026, 5, 1, 10, 0),
                LocalDateTime.of(2026, 5, 1, 10, 0)
        );

        when(organizationApplicationRepository.findAllByApplicantUserId(applicantUserId))
                .thenReturn(List.of(application));

        // when
        List<OrganizationApplication> result = organizationApplicationQueryService.findMyOrganizationApplications(applicantUserId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getApplicantUserId()).isEqualTo(applicantUserId);
        assertThat(result.get(0).getBusinessName()).isEqualTo("짐짝헬스장");
        assertThat(result.get(0).getStatus()).isEqualTo(OrganizationApplicationStatus.PENDING);

        verify(organizationApplicationRepository).findAllByApplicantUserId(applicantUserId);
    }

    @Test
    @DisplayName("신청 내역이 없으면 빈 리스트를 반환한다")
    void findMyOrganizationApplications_empty() {
        // given
        Long applicantUserId = 1L;

        when(organizationApplicationRepository.findAllByApplicantUserId(applicantUserId))
                .thenReturn(List.of());

        // when
        List<OrganizationApplication> result = organizationApplicationQueryService.findMyOrganizationApplications(applicantUserId);

        // then
        assertThat(result).isEmpty();

        verify(organizationApplicationRepository).findAllByApplicantUserId(applicantUserId);
    }
}
