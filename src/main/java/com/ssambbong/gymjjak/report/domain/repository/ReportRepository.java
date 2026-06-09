package com.ssambbong.gymjjak.report.domain.repository;

import com.ssambbong.gymjjak.report.domain.model.Report;
import com.ssambbong.gymjjak.report.domain.model.ReportStatus;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {

    Optional<Report> findById(Long reportId);

    Report save(Report report);

    List<Report> findAllByReportGroupId(Long reportGroupId);

    // 중복 신고 검증
    boolean existsByReporterIdAndReportGroupId(Long reporterId, Long reportGroupId);

    long countByStatus(ReportStatus reportStatus);

    long countAll();

    // hard Delete 스케줄러
    int hardDeleteByReportGroupIds(List<Long> reportGroupIds);

}
