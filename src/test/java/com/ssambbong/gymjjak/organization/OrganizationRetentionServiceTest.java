package com.ssambbong.gymjjak.organization;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.organization.scheduler.application.retention.OrganizationRetentionProperties;
import com.ssambbong.gymjjak.organization.scheduler.application.retention.OrganizationRetentionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

public class OrganizationRetentionServiceTest {

    private final OrganizationRetentionProperties properties =
            new OrganizationRetentionProperties(90, 500);

    private final OrganizationTrainerRepository trainerRepository =
            mock(OrganizationTrainerRepository.class);

    private final OrganizationApplicationRepository applicationRepository =
            mock(OrganizationApplicationRepository.class);

    private final OrganizationRepository organizationRepository =
            mock(OrganizationRepository.class);

    private final OrganizationRetentionService service =
            new OrganizationRetentionService(properties, trainerRepository, applicationRepository, organizationRepository);

    @Test
    @DisplayName("보관 기간이 지난 소속 트레이너를 hard delete 한다")
    void hardDeleteExpired_trainer_success() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 28, 3, 0);
        LocalDateTime threshold = now.minusDays(90);

        when(trainerRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of(1L, 2L));
        when(trainerRepository.hardDeleteByIds(List.of(1L, 2L))).thenReturn(2);
        when(applicationRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());
        when(organizationRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());

        // when
        RetentionJobResult result = service.hardDeleteExpired(now);

        // then
        assertThat(result.jobName()).isEqualTo("organization-retention");
        assertThat(result.candidateCount()).isEqualTo(2);
        assertThat(result.deletedParentCount()).isEqualTo(2);

        verify(trainerRepository).hardDeleteByIds(List.of(1L, 2L));
        verify(applicationRepository, never()).hardDeleteByIds(anyList());
        verify(organizationRepository, never()).hardDeleteByIds(anyList());
    }

    @Test
    @DisplayName("보관 기간이 지난 조직 신청서를 hard delete 한다")
    void hardDeleteExpired_application_success() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 28, 3, 0);
        LocalDateTime threshold = now.minusDays(90);

        when(trainerRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());
        when(applicationRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of(10L, 11L));
        when(applicationRepository.hardDeleteByIds(List.of(10L, 11L))).thenReturn(2);
        when(organizationRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());

        // when
        RetentionJobResult result = service.hardDeleteExpired(now);

        // then
        assertThat(result.candidateCount()).isEqualTo(2);
        assertThat(result.deletedParentCount()).isEqualTo(2);

        verify(trainerRepository, never()).hardDeleteByIds(anyList());
        verify(applicationRepository).hardDeleteByIds(List.of(10L, 11L));
        verify(organizationRepository, never()).hardDeleteByIds(anyList());
    }

    @Test
    @DisplayName("보관 기간이 지난 조직을 hard delete 할 때 연관 신청서도 함께 삭제한다")
    void hardDeleteExpired_organization_success() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 28, 3, 0);
        LocalDateTime threshold = now.minusDays(90);

        when(trainerRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());
        when(applicationRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());
        when(organizationRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of(20L));
        when(organizationRepository.findApplicationIdsByOrganizationIds(List.of(20L))).thenReturn(List.of(30L));
        when(organizationRepository.hardDeleteByIds(List.of(20L))).thenReturn(1);

        // when
        RetentionJobResult result = service.hardDeleteExpired(now);

        // then
        assertThat(result.candidateCount()).isEqualTo(1);
        assertThat(result.deletedParentCount()).isEqualTo(1);

        InOrder inOrder = inOrder(organizationRepository, applicationRepository);
        inOrder.verify(organizationRepository).hardDeleteByIds(List.of(20L));
        inOrder.verify(applicationRepository).hardDeleteByIds(List.of(30L));
    }

    @Test
    @DisplayName("보관 기간이 지나지 않은 데이터는 삭제하지 않는다")
    void hardDeleteExpired_empty() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 28, 3, 0);
        LocalDateTime threshold = now.minusDays(90);

        when(trainerRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());
        when(applicationRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());
        when(organizationRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());

        // when
        RetentionJobResult result = service.hardDeleteExpired(now);

        // then
        assertThat(result.jobName()).isEqualTo("organization-retention");
        assertThat(result.candidateCount()).isZero();
        assertThat(result.deletedParentCount()).isZero();

        verify(trainerRepository, never()).hardDeleteByIds(anyList());
        verify(applicationRepository, never()).hardDeleteByIds(anyList());
        verify(organizationRepository, never()).hardDeleteByIds(anyList());
    }
}
