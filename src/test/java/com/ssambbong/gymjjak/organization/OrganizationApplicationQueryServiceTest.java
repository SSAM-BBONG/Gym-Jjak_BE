package com.ssambbong.gymjjak.organization;

import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListQuery;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListResult;
import com.ssambbong.gymjjak.organization.organizationApplication.application.service.OrganizationApplicationQueryService;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.OrganizationApplicationNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationApplicationQueryServiceTest {

    @Mock private OrganizationApplicationRepository organizationApplicationRepository;

    @InjectMocks
    private OrganizationApplicationQueryService organizationApplicationQueryService;

    private OrganizationApplication application(Long applicationId, Long applicantUserId) {
        return OrganizationApplication.restore(
                applicationId,
                applicantUserId,
                "gymjjak123",
                1L,
                "1234567890",
                "짐짝헬스장",
                "홍길동",
                "010-1234-5678",
                LocalDate.of(2024, 1, 1),
                "서울시 강남구 테헤란로 1",
                null, null,
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276"),
                null, null, null,
                "02-1234-5678",
                OrganizationApplicationStatus.PENDING,
                LocalDateTime.of(2026, 5, 1, 10, 0),
                LocalDateTime.of(2026, 5, 1, 10, 0),
                null, null, null
        );
    }

    @Test
    @DisplayName("내 신청 목록 조회에 성공한다")
    void findMyOrganizationApplications_success() {
        // given
        Long applicantUserId = 1L;
        when(organizationApplicationRepository.findAllByApplicantUserId(applicantUserId))
                .thenReturn(List.of(application(1L, applicantUserId)));

        // when
        List<OrganizationApplication> result = organizationApplicationQueryService.findMyOrganizationApplications(applicantUserId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getApplicantUserId()).isEqualTo(applicantUserId);
        assertThat(result.get(0).getBusinessName()).isEqualTo("짐짝헬스장");
    }

    @Test
    @DisplayName("신청 내역이 없으면 빈 리스트를 반환한다")
    void findMyOrganizationApplications_empty() {
        // given
        when(organizationApplicationRepository.findAllByApplicantUserId(1L))
                .thenReturn(List.of());

        // when
        List<OrganizationApplication> result = organizationApplicationQueryService.findMyOrganizationApplications(1L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("본인의 조직 신청 상세 조회에 성공한다")
    void findOrganizationApplicationDetails_success() {
        // given
        Long applicationId = 1L;
        Long userId = 1L;

        when(organizationApplicationRepository.findByIdAndApplicantUserId(applicationId, userId))
                .thenReturn(Optional.of(application(applicationId, userId)));

        // when
        OrganizationApplication result = organizationApplicationQueryService.findOrganizationApplicationDetails(applicationId, userId, false);

        // then
        assertThat(result.getOrganizationApplicationId()).isEqualTo(applicationId);
        assertThat(result.getBusinessName()).isEqualTo("짐짝헬스장");
    }

    @Test
    @DisplayName("ADMIN은 타인의 신청 상세를 조회할 수 있다")
    void findOrganizationApplicationDetails_success_admin() {
        // given
        Long applicationId = 1L;
        Long adminUserId = 99L;

        when(organizationApplicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application(applicationId, 1L)));

        // when
        OrganizationApplication result = organizationApplicationQueryService.findOrganizationApplicationDetails(applicationId, adminUserId, true);

        // then
        assertThat(result.getOrganizationApplicationId()).isEqualTo(applicationId);
    }

    @Test
    @DisplayName("존재하지 않는 신청 ID로 조회하면 OrganizationApplicationNotFoundException이 발생한다")
    void findOrganizationApplicationDetails_fail_notFound() {
        // given
        when(organizationApplicationRepository.findByIdAndApplicantUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                organizationApplicationQueryService.findOrganizationApplicationDetails(999L, 1L, false)
        ).isInstanceOf(OrganizationApplicationNotFoundException.class);
    }

    @Test
    @DisplayName("타인의 신청을 조회하면 OrganizationApplicationNotFoundException이 발생한다")
    void findOrganizationApplicationDetails_fail_otherUserAccess() {
        // given
        Long applicationId = 1L;
        Long otherUserId = 2L;

        when(organizationApplicationRepository.findByIdAndApplicantUserId(applicationId, otherUserId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                organizationApplicationQueryService.findOrganizationApplicationDetails(applicationId, otherUserId, false)
        ).isInstanceOf(OrganizationApplicationNotFoundException.class);
    }

    @Test
    @DisplayName("PENDING 상태 신청 목록을 페이지네이션으로 조회한다")
    void findPendingOrganizationApplications_success() {
        // given
        ApplicationListQuery query = new ApplicationListQuery(1, 10);
        ApplicationListResult expected = new ApplicationListResult(
                List.of(application(1L, 1L)),
                1, 10, 1L, 1
        );

        when(organizationApplicationRepository.findAllByStatus(OrganizationApplicationStatus.PENDING, query))
                .thenReturn(expected);

        // when
        ApplicationListResult result = organizationApplicationQueryService.findPendingOrganizationApplications(query);

        // then
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.items()).hasSize(1);
    }
}
