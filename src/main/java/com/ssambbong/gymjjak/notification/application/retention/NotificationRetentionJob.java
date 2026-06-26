package com.ssambbong.gymjjak.notification.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * GlobalRetentionScheduler
 * → List<RetentionJob> retentionJobs 자동 주입
 * → NotificationRetentionJob.run(now)
 * → NotificationRetentionService.hardDeleteExpiredNotifications(now)
 */
@Component
@RequiredArgsConstructor
public class NotificationRetentionJob implements RetentionJob {

    private final NotificationRetentionService  notificationRetentionService;

    @Override
    public String name() {
        return NotificationRetentionService.JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return notificationRetentionService.hardDeleteExpiredNotifications(now);
    }
}
