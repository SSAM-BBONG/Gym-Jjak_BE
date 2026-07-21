package com.ssambbong.gymjjak.report;

import com.ssambbong.gymjjak.report.application.query.AdminReportSnapshotResult;
import com.ssambbong.gymjjak.report.application.service.ReportGroupQueryService;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportGroupQueryServiceTest {

    @Mock
    private ReportGroupRepository reportGroupRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportGroupQueryService reportGroupQueryService;

    @Test
    void findReportSnapshot_returnsStoredSnapshotOfActiveReportGroup() {
        // 활성 신고 그룹에 저장된 스냅샷으로 모달 조회 결과를 만듭니다.
        ReportGroup reportGroup = ReportGroup.reconstruct(
                10L,
                "RPT-10",
                ReportTargetType.COMMENT,
                301L,
                7L,
                "댓글",
                "신고된 댓글 내용",
                null,
                1,
                1,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.NONE,
                null,
                LocalDateTime.of(2026, 7, 21, 10, 0),
                LocalDateTime.of(2026, 7, 21, 10, 0),
                null
        );
        when(reportGroupRepository.findActiveById(10L)).thenReturn(Optional.of(reportGroup));

        AdminReportSnapshotResult result = reportGroupQueryService.findReportSnapshot(10L);

        assertThat(result).isEqualTo(new AdminReportSnapshotResult(
                10L,
                ReportTargetType.COMMENT,
                301L,
                "댓글",
                "신고된 댓글 내용",
                null
        ));
    }

    @Test
    void findReportSnapshot_throwsWhenActiveReportGroupDoesNotExist() {
        when(reportGroupRepository.findActiveById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportGroupQueryService.findReportSnapshot(999L))
                .isInstanceOf(ReportGroupNotFoundException.class);
    }
}
