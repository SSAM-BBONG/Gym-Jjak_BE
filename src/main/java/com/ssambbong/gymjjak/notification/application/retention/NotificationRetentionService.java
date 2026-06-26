package com.ssambbong.gymjjak.notification.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationRetentionService {

    public static final String JOB_NAME = "notification_retention";

    private final NotificationRetentionProperties properties;
    private final NotificationRepository repository;

    @Transactional
    public RetentionJobResult hardDeleteExpiredNotifications(LocalDateTime now) {
        // 기준일
        LocalDateTime threshold = properties.threshold(now);

        // 삭제할 Id list
        List<Long> candidateIds =
                repository.findHardDeleteCandidateIds(
                        threshold,
                        properties.batchSize()
                );

        if (candidateIds.isEmpty()) {
            log.info(
                    "event=notification_retention_empty threshold={}, periodDays={}, batchSize={}",
                    threshold,
                    properties.periodDays(),
                    properties.batchSize()
            );

            return RetentionJobResult.empty(JOB_NAME);
        }

        int deletedNotifications =
                repository.hardDeleteByIds(candidateIds, threshold);

        log.info(
                "event=notification_retention_completed threshold={}, candidateCount={}, deletedNotifications={}",
                threshold,
                candidateIds.size(),
                deletedNotifications
        );

        return new RetentionJobResult(
                JOB_NAME,
                candidateIds.size(),
                0,
                deletedNotifications
        );
    }
}
