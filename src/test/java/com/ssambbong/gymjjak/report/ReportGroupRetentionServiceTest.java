package com.ssambbong.gymjjak.report;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.report.application.retention.ReportGroupRetentionProperties;
import com.ssambbong.gymjjak.report.application.retention.ReportGroupRetentionService;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReportGroupRetentionServiceTest {

    private final ReportGroupRepository reportGroupRepository = mock(ReportGroupRepository.class);
    private final ReportRepository reportRepository = mock(ReportRepository.class);

    private final ReportGroupRetentionProperties properties =
            new ReportGroupRetentionProperties(1, 500);

    private final ReportGroupRetentionService service =
            new ReportGroupRetentionService(
                    properties,
                    reportGroupRepository,
                    reportRepository
            );

    @Test
    @DisplayName("수동 블라인드 처리 완료 후 보관 기간이 지난 신고그룹과 하위 신고를 hard delete 한다")
    void hardDeleteExpiredReportGroups_success() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 9, 3, 0);
        LocalDateTime expectedThreshold = now.minusDays(1);

        List<Long> candidateIds = List.of(1L);

        when(reportGroupRepository.findManualBlindedResolvedHardDeleteCandidateIds(
                expectedThreshold,
                500
        )).thenReturn(candidateIds);

        when(reportRepository.hardDeleteByReportGroupIds(candidateIds)).thenReturn(4);
        when(reportGroupRepository.hardDeleteByIds(candidateIds)).thenReturn(1);

        // when
        RetentionJobResult result = service.hardDeleteExpiredReportGroups(now);

        // then
        assertThat(result.jobName()).isEqualTo("report-group-retention");
        assertThat(result.candidateCount()).isEqualTo(1);
        assertThat(result.deletedChildCount()).isEqualTo(4);
        assertThat(result.deletedParentCount()).isEqualTo(1);

        verify(reportRepository).hardDeleteByReportGroupIds(candidateIds);
        verify(reportGroupRepository).hardDeleteByIds(candidateIds);
    }

    @Test
    @DisplayName("삭제 후보가 없으면 reports와 report_groups를 삭제하지 않는다")
    void hardDeleteExpiredReportGroups_empty() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 9, 3, 0);
        LocalDateTime expectedThreshold = now.minusDays(1);

        when(reportGroupRepository.findManualBlindedResolvedHardDeleteCandidateIds(
                expectedThreshold,
                500
        )).thenReturn(List.of());

        // when
        RetentionJobResult result = service.hardDeleteExpiredReportGroups(now);

        // then
        assertThat(result.jobName()).isEqualTo("report-group-retention");
        assertThat(result.candidateCount()).isZero();
        assertThat(result.deletedChildCount()).isZero();
        assertThat(result.deletedParentCount()).isZero();

        verify(reportRepository, never()).hardDeleteByReportGroupIds(anyList());
        verify(reportGroupRepository, never()).hardDeleteByIds(anyList());
    }
}
