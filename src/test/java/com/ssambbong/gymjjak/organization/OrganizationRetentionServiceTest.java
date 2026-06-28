package com.ssambbong.gymjjak.organization;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.organization.scheduler.application.retention.OrganizationRetentionProperties;
import com.ssambbong.gymjjak.organization.scheduler.application.retention.OrganizationRetentionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrganizationRetentionServiceTest {

    private final OrganizationTrainerRepository organizationTrainerRepository = mock(OrganizationTrainerRepository.class);

    private final OrganizationRetentionProperties properties =
            new OrganizationRetentionProperties(90, 500);

    private final OrganizationRetentionService service =
            new OrganizationRetentionService(properties, organizationTrainerRepository);

    @Test
    @DisplayName("보관 기간이 지난 소속 트레이너를 hard delete 한다")
    void hardDeleteExpired_success() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 28, 3, 0);
        LocalDateTime expectedThreshold = now.minusDays(90);

        List<Long> candidateIds = List.of(1L, 2L);

        when(organizationTrainerRepository.findHardDeleteCandidateIds(expectedThreshold, 500))
                .thenReturn(candidateIds);
        when(organizationTrainerRepository.hardDeleteByIds(candidateIds))
                .thenReturn(2);

        // when
        RetentionJobResult result = service.hardDeleteExpired(now);

        // then
        assertThat(result.jobName()).isEqualTo("organization-retention");
        assertThat(result.candidateCount()).isEqualTo(2);
        assertThat(result.deletedParentCount()).isEqualTo(2);

        verify(organizationTrainerRepository).hardDeleteByIds(candidateIds);
    }

    @Test
    @DisplayName("보관 기간이 지나지 않은 소속 트레이너는 삭제하지 않는다")
    void hardDeleteExpired_empty() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 28, 3, 0);
        LocalDateTime expectedThreshold = now.minusDays(90);

        when(organizationTrainerRepository.findHardDeleteCandidateIds(expectedThreshold, 500))
                .thenReturn(List.of());

        // when
        RetentionJobResult result = service.hardDeleteExpired(now);

        // then
        assertThat(result.jobName()).isEqualTo("organization-retention");
        assertThat(result.candidateCount()).isZero();
        assertThat(result.deletedParentCount()).isZero();

        verify(organizationTrainerRepository, never()).hardDeleteByIds(anyList());
    }
}
