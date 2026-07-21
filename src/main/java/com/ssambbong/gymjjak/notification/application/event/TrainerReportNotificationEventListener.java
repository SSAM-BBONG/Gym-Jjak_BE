package com.ssambbong.gymjjak.notification.application.event;

import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import com.ssambbong.gymjjak.trainerReport.application.event.TrainerReportGeneratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerReportNotificationEventListener {

    private final NotificationEventProcessor processor;

    // 트레이너 리포트 생성 완료 알림 — 트레이너 수신
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTrainerReportGenerated(TrainerReportGeneratedEvent event) {
        log.info("event=trainer_report_generated_notification_received receiverId={}, trainerReportId={}",
                event.receiverId(), event.trainerReportId());
        processor.createSafely(
                event.receiverId(),
                NotificationType.TRAINER_REPORT_GENERATED,
                event.trainerReportId(),
                event.occurredAt());
    }
}
