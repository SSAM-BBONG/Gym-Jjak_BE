package com.ssambbong.gymjjak.report.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/* Comment
*   RetentionJob 구현 Adapter, 해당 클래스를 Bean으로 등록 된다.
*   -> GlobalRetentionScheduler 클래스의 retentionJobs 자동 주입
*   -> service로 위임
* */

@Component
@RequiredArgsConstructor
public class ReportGroupRetentionJob implements RetentionJob {

    private final ReportGroupRetentionService reportGroupRetentionService;

    @Override
    public String name() {
        return ReportGroupRetentionService.JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return reportGroupRetentionService.hardDeleteExpiredReportGroups(now);
    }
}
