package com.ssambbong.gymjjak.notification.application.event;

import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import com.ssambbong.gymjjak.pt.feedback.application.event.FeedbackCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackNotificationEventListener {

    private final NotificationEventProcessor processor;

    // 피드백 등록 알림 수신
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFeedbackCreated(FeedbackCreatedEvent event) {

        log.info("event=feedback_created_notification_received receiverId={}, feedbackId={}",
                event.receiverId(), event.feedbackId());
        processor.createSafely(
                event.receiverId(),
                NotificationType.FEEDBACK_CREATED,
                event.feedbackId(),
                event.occurredAt()
        );
    }
}
