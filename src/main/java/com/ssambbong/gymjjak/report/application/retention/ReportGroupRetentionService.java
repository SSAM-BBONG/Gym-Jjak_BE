package com.ssambbong.gymjjak.report.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGroupRetentionService {

    public static final String JOB_NAME = "report-group-retention";

    private final ReportGroupRetentionProperties properties;

    private final ReportGroupRepository reportGroupRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpiredReportGroups(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);

        List<Long> candidateIds =
                reportGroupRepository.findManualBlindedResolvedHardDeleteCandidateIds(
                        threshold,
                        properties.batchSize()
                );

        if (candidateIds.isEmpty()) {
            log.info(
                    "event=report-group-retention-empty threshold: {}, periodDays: {}, batchSize: {}",
                    threshold,
                    properties.periodDays(),
                    properties.batchSize()
            );

            return RetentionJobResult.empty(JOB_NAME);
        }

        int deletedReports = reportRepository.hardDeleteByReportGroupIds(candidateIds);
        int deletedReportGroups = reportGroupRepository.hardDeleteByIds(candidateIds);

        log.info(
                "event=report_group_retention_completed threshold: {}, candidateCount: {}, deletedReports: {}, deletedReportGroups: {}",
                threshold,
                candidateIds.size(),
                deletedReports,
                deletedReportGroups
        );

        return new RetentionJobResult(
                JOB_NAME,
                candidateIds.size(),
                deletedReports,
                deletedReportGroups
        );
    }
}
