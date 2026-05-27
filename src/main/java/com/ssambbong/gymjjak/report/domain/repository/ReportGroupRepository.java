package com.ssambbong.gymjjak.report.domain.repository;

import com.ssambbong.gymjjak.report.application.query.AdminReportListQuery;
import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;

import java.util.Optional;

public interface ReportGroupRepository {

    ReportGroup save(ReportGroup reportGroup);

    Optional<ReportGroup> findById(Long reportGroupId);

    AdminReportListResult findAdminReportList(AdminReportListQuery query);
}
