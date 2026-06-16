package com.ssambbong.gymjjak.global.infrastructure.scheduler;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalRetentionScheduler {

    private final List<RetentionJob> retentionJobs;
    private final Clock clock;

    @Scheduled(
            cron = "${app.scheduler.retention.cron}",
            zone = "${app.scheduler.retention.zone}"
    )
    public void runRetentionJobs() {
        LocalDateTime now = LocalDateTime.now(clock);

        if (retentionJobs.isEmpty()) {
            log.debug("event=scheduler-retention-no-jobs 등록된 retention job이 없습니다.");
            return;
        }

        for (RetentionJob job : retentionJobs) {
            runJob(job, now);
        }
    }

    private void runJob(RetentionJob job, LocalDateTime now) {
        try {
            RetentionJobResult result = job.run(now);

            log.info(
                    "event=scheduler-retention-succeeded jobName: {}, candidateCount: {}, deletedChildCount: {}, deletedParentCount: {}",
                    result.jobName(),
                    result.candidateCount(),
                    result.deletedChildCount(),
                    result.deletedParentCount()
            );

        } catch (Exception e) {
            log.error("event=scheduler-retention-fail jobName: {}", job.name(), e);
        }
    }
}
