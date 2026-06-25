package com.ssambbong.gymjjak.notification.application.event;

import com.ssambbong.gymjjak.notification.application.port.out.NotificationRealtimeSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRealtimeSender notificationRealtimeSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationCreatedEvent event) {
        try {
            notificationRealtimeSender.sendToUser(
                    event.receiverId(),
                    event.notification()
            );
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_realtime_send_failed, " +
                            "notificationId={}, receiverId={}, type={}",
                    event.notification().notificationId(),
                    event.receiverId(),
                    event.notification().type(),
                    exception
            );
        }
    }

}
