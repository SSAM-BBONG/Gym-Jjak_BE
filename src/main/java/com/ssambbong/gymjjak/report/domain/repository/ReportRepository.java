package com.ssambbong.gymjjak.report.domain.repository;

import com.ssambbong.gymjjak.report.domain.model.Report;

import java.util.Optional;

public interface ReportRepository {

    Optional<Report> findById(Long reportGroupId);

    Report save(Report report);
}
