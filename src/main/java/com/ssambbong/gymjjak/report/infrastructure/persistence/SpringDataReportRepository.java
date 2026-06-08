package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.application.query.AdminReportReasonItem;
import com.ssambbong.gymjjak.report.domain.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataReportRepository extends JpaRepository<ReportJpaEntity, Long> {

    // 최신 신고일 조회
    Optional<ReportJpaEntity> findTopByReportGroupIdOrderByCreatedAtDesc(Long reportGroupId);

    // 개별 신고 목록 최신순 정렬 조회
    List<ReportJpaEntity> findByReportGroupIdOrderByCreatedAtDesc(Long reportGroupId);

    // 같은 사용자가 같은 신고그룹을 이미 신고했는지 검증
    boolean existsByReporterIdAndReportGroupId(Long reporterId, Long reportGroupId);

    long countByStatus(ReportStatus status);
}
