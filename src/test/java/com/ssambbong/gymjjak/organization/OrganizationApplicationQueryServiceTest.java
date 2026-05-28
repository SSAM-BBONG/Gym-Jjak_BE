package com.ssambbong.gymjjak.organization;

import com.ssambbong.gymjjak.organization.application.service.OrganizationApplicationQueryService;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.exception.OrganizationApplicationAccessDeniedException;
import com.ssambbong.gymjjak.organization.exception.OrganizationApplicationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    @DisplayName("조직 신청 상세 조회에 성공한다")
    void findOrganizationApplicationDetails_success() {
        // given
        Long organizationApplicationId = 1L;
        Long requestUserId = 1L;

        OrganizationApplication application = OrganizationApplication.restore(
                organizationApplicationId,
                requestUserId,
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

        when(organizationApplicationRepository.findById(organizationApplicationId))
                .thenReturn(Optional.of(application));

        // when
        OrganizationApplication result = organizationApplicationQueryService.findOrganizationApplicationDetails(organizationApplicationId, requestUserId, false);

        // then
        assertThat(result.getOrganizationApplicationId()).isEqualTo(organizationApplicationId);
        assertThat(result.getBusinessName()).isEqualTo("짐짝헬스장");
        assertThat(result.getRepresentativeName()).isEqualTo("홍길동");
        assertThat(result.getStatus()).isEqualTo(OrganizationApplicationStatus.PENDING);

        verify(organizationApplicationRepository).findById(organizationApplicationId);
    }

    @Test
    @DisplayName("ADMIN은 타인의 신청 상세를 조회할 수 있다")
    void findOrganizationApplicationDetails_success_admin() {
        // given
        Long organizationApplicationId = 1L;
        Long applicantUserId = 1L;
        Long adminUserId = 99L;

        OrganizationApplication application = OrganizationApplication.restore(
                organizationApplicationId,
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

        when(organizationApplicationRepository.findById(organizationApplicationId))
                .thenReturn(Optional.of(application));

        // when
        OrganizationApplication result = organizationApplicationQueryService.findOrganizationApplicationDetails(organizationApplicationId, adminUserId, true);

        // then
        assertThat(result.getOrganizationApplicationId()).isEqualTo(organizationApplicationId);

        verify(organizationApplicationRepository).findById(organizationApplicationId);
    }

    @Test
    @DisplayName("존재하지 않는 신청 ID로 조회하면 OrganizationApplicationNotFoundException이 발생한다")
    void findOrganizationApplicationDetails_fail_notFound() {
        // given
        Long organizationApplicationId = 999L;

        when(organizationApplicationRepository.findById(organizationApplicationId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                organizationApplicationQueryService.findOrganizationApplicationDetails(organizationApplicationId, 1L, false)
        ).isInstanceOf(OrganizationApplicationNotFoundException.class);

        verify(organizationApplicationRepository).findById(organizationApplicationId);
    }

    @Test
    @DisplayName("타인의 신청을 조회하면 OrganizationApplicationAccessDeniedException이 발생한다")
    void findOrganizationApplicationDetails_fail_accessDenied() {
        // given
        Long organizationApplicationId = 1L;
        Long applicantUserId = 1L;
        Long otherUserId = 2L;

        OrganizationApplication application = OrganizationApplication.restore(
                organizationApplicationId,
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

        when(organizationApplicationRepository.findById(organizationApplicationId))
                .thenReturn(Optional.of(application));

        // when & then
        assertThatThrownBy(() ->
                organizationApplicationQueryService.findOrganizationApplicationDetails(organizationApplicationId, otherUserId, false)
        ).isInstanceOf(OrganizationApplicationAccessDeniedException.class);

        verify(organizationApplicationRepository).findById(organizationApplicationId);
    }
}
