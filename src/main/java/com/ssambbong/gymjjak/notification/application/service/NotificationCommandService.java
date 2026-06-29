package com.ssambbong.gymjjak.notification.application.service;

import com.ssambbong.gymjjak.notification.application.command.CreateNotificationCommand;
import com.ssambbong.gymjjak.notification.application.event.NotificationCreatedEvent;
import com.ssambbong.gymjjak.notification.application.result.NotificationResult;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationCommandUseCase;
import com.ssambbong.gymjjak.notification.domain.exception.InvalidNotificationException;
import com.ssambbong.gymjjak.notification.domain.model.Notification;
import com.ssambbong.gymjjak.notification.domain.repository.NotificationRepository;
import com.ssambbong.gymjjak.notification.infrastructure.metrics.NotificationMetric;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 생성 이벤트 서비스
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCommandService implements NotificationCommandUseCase {

    private final NotificationRepository repository;
    private final ApplicationEventPublisher publisher;
    private final NotificationMetric notificationMetric;

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

        // 시간 측정
        Timer.Sample createTimer =
                notificationMetric.startTimer();

        String outcome = notificationMetric.success();

        // 알림을 서버 DB에 만드는 데 걸린 시간
        try {
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

            // DB 저장 후, 이벤트 발행
            publisher.publishEvent(
                    new NotificationCreatedEvent(
                            result.receiverId(),
                            result
                    )
            );

            log.info(
                    "event=notification_create_succeeded, notificationId={}, receiverId={}, type={}",
                    savedNotification.getNotificationId(),
                    savedNotification.getReceiverId(),
                    savedNotification.getType()
            );

            return result;
        } catch (RuntimeException exception) {
            outcome = notificationMetric.failure();
            throw exception;
        } finally {
            notificationMetric.recordCommandDurationSafely(
                    createTimer,
                    "create",
                    outcome
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
