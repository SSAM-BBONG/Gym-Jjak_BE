package com.ssambbong.gymjjak.notification.application.event;

import com.ssambbong.gymjjak.notification.application.command.CreateNotificationCommand;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationCommandUseCase;
import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventProcessor {

    private static final ZoneId SERVICE_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final NotificationCommandUseCase notificationCommandUseCase;

    public void createSafely(
            Long receiverId,
            NotificationType notificationType,
            Long targetId,
            Instant occurredAt
    ) {
        try {
            notificationCommandUseCase.createNotification(
                    new CreateNotificationCommand(
                            receiverId,
                            notificationType,
                            targetId,
                            toLocalDateTime(occurredAt)
                    )
            );

        } catch (RuntimeException exception) {
            log.error(
                    "event=notification_create_failed, receiverId={}, " +
                            "notificationType={}, targetId={}, eventAt={}",
                    receiverId,
                    notificationType,
                    targetId,
                    occurredAt,
                    exception
            );
        }
    }

    private LocalDateTime toLocalDateTime(Instant occurredAt) {
        if (occurredAt == null) {
            return null;
        }

        return LocalDateTime.ofInstant(
                occurredAt,
                SERVICE_ZONE_ID
        );
    }
}
