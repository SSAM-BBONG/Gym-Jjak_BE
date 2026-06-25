package com.ssambbong.gymjjak.notification.application.event;

import com.ssambbong.gymjjak.notification.application.command.CreateNotificationCommand;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationCommandUseCase;
import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.event.TrainerApplicationApprovedEvent;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.event.TrainerApplicationRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainerApplicationNotificationEventListener {

    private final NotificationEventProcessor processor;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTrainerApplicationApproved(
            TrainerApplicationApprovedEvent event
    ) {
        log.info(
                "event=trainer_application_approved_notification_approved_received, " +
                        "receiverId={}, trainerApplicationId={}, trainerProfileId={}",
                event.receiverId(),
                event.trainerApplicationId(),
                event.trainerProfileId()
        );

        processor.createSafely(
                event.receiverId(),
                NotificationType.TRAINER_APPLICATION_APPROVED,
                event.trainerApplicationId(),
                event.occurredAt()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTrainerApplicationRejected(
            TrainerApplicationRejectedEvent event
    ) {
        log.info(
                "event=trainer_application_rejected_notification_event_received, " +
                        "receiverId={}, trainerApplicationId={}",
                event.receiverId(),
                event.trainerApplicationId()
        );

        processor.createSafely(
                event.receiverId(),
                NotificationType.TRAINER_APPLICATION_REJECTED,
                event.trainerApplicationId(),
                event.occurredAt()
        );
    }
}
