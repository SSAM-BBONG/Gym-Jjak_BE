package com.ssambbong.gymjjak.report.domain.repository;

import com.ssambbong.gymjjak.report.domain.model.Report;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {

    Optional<Report> findById(Long reportId);

    Report save(Report report);

    List<Report> findAllByReportGroupId(Long reportGroupId);

    boolean existsByReportIdAndReportGroupId(Long reportId, Long reportGroupId);
}
