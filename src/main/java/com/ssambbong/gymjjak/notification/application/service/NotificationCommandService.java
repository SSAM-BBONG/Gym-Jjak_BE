package com.ssambbong.gymjjak.notification.application.service;

import com.ssambbong.gymjjak.notification.application.command.CreateNotificationCommand;
import com.ssambbong.gymjjak.notification.application.port.out.NotificationRealtimeSender;
import com.ssambbong.gymjjak.notification.application.result.NotificationResult;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationCommandUseCase;
import com.ssambbong.gymjjak.notification.domain.exception.InvalidNotificationException;
import com.ssambbong.gymjjak.notification.domain.model.Notification;
import com.ssambbong.gymjjak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCommandService implements NotificationCommandUseCase {

    private final NotificationRepository repository;
    private final NotificationRealtimeSender notificationRealtimeSender;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public NotificationResult createNotification(CreateNotificationCommand command) {
        validateCreateCommand(command);

        log.info(
                "event=notification_create_started, receiverId={}, type={}, targetId={}",
                command.receiverId(),
                command.type(),
                command.targetId()
        );

        // 알림 생성
        Notification notification = Notification.create(
                command.receiverId(),
                command.type(),
                command.targetId(),
                command.eventAt()
        );

        Notification savedNotification = repository.save(notification);

        NotificationResult result =
                NotificationResult.from(savedNotification);

        // DB 저장 후, 알림 Websocket 전송
        sendRealtimeNotificationSafely(result);

        log.info(
                "event=notification_create_succeeded, notificationId={}, receiverId={}, type={}",
                savedNotification.getNotificationId(),
                savedNotification.getReceiverId(),
                savedNotification.getType()
        );

        return result;
    }

    private void sendRealtimeNotificationSafely(NotificationResult notification) {
        try {
            notificationRealtimeSender.sendToUser(
                    notification.receiverId(),
                    notification
            );
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_realtime_send_failed, " +
                            "notificationId={}, receiverId={}, type={}",
                    notification.notificationId(),
                    notification.receiverId(),
                    notification.type(),
                    exception
            );
        }
    }

    private void validateCreateCommand(CreateNotificationCommand command) {
        if (command == null) {
            throw new InvalidNotificationException(
                    "알림 생성 command는 필수입니다."
            );
        }

        if (command.receiverId() == null || command.receiverId() <= 0) {
            throw new InvalidNotificationException(
                    "receiverId는 1이상 이여야합니다."
            );
        }

        if (command.type() == null) {
            throw new InvalidNotificationException(
                    "커맨드 타입은 null일 수 없습니다."
            );
        }
    }
}
