package com.ssambbong.gymjjak.notification.application.service;

import com.ssambbong.gymjjak.notification.application.command.MarkNotificationReadCommand;
import com.ssambbong.gymjjak.notification.application.result.MarkNotificationReadResult;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationUserCommandUseCase;
import com.ssambbong.gymjjak.notification.domain.exception.ForbiddenNotificationAccessException;
import com.ssambbong.gymjjak.notification.domain.exception.InvalidNotificationException;
import com.ssambbong.gymjjak.notification.domain.exception.NotificationNotFoundException;
import com.ssambbong.gymjjak.notification.domain.model.Notification;
import com.ssambbong.gymjjak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationUserCommandService implements NotificationUserCommandUseCase {

    private final NotificationRepository notificationRepository;

    @Override
    public MarkNotificationReadResult readNotifications(MarkNotificationReadCommand command) {
        // command 검증
        validateReadCommand(command);

        log.info(
                "event=notification_read_started, requesterId={}, requestedCount={}",
                command.requesterId(),
                command.notificationIds().size()
        );

        // 중복 제거 후 정렬
        List<Long> notificationIds = removeDuplicateIds(
                command.notificationIds()
        );

        LocalDateTime now = LocalDateTime.now();

        List<Long> readNotificationIds = new ArrayList<>();

        for (Long notificationId : notificationIds) {
            Notification notification =
                    notificationRepository.findById(notificationId)
                            .orElseThrow(() ->
                                    new NotificationNotFoundException(notificationId)
                            );

            // 본인 알림 검증
            validateReadableNotification(
                    notification,
                    command.requesterId(),
                    now
            );

            // 읽기 처리
            Notification readNotification = notification.markAsRead();

            // 알림 저장
            if (!notification.isRead()) {
                notificationRepository.save(readNotification);
            }

            // list에 저장
            readNotificationIds.add(notificationId);
        }

        log.info(
                "event=notification_read_succeeded, requesterId={}, requestedCount={}, readCount={}",
                command.requesterId(),
                command.notificationIds().size(),
                readNotificationIds.size()
        );

        return MarkNotificationReadResult.builder()
                .readNotificationIds(readNotificationIds)
                .build();
    }

    private void validateReadableNotification(
            Notification notification,
            Long requesterId,
            LocalDateTime now
    ) {
        // 본인 알림 검증
        if (!notification.isOwnedBy(requesterId)) {
            throw new ForbiddenNotificationAccessException(
                    requesterId,
                    notification.getNotificationId()
            );
        }

        // 삭제되거나, 만료된 알림 검증
        if (notification.isDeleted() || notification.isExpired(now)) {
            throw new NotificationNotFoundException(
                    notification.getNotificationId()
            );
        }
    }

    private List<Long> removeDuplicateIds(List<Long> notificationIds) {
        // 중복 제거 후 list로
        return notificationIds.stream()
                .distinct().toList();
    }

    private void validateReadCommand(MarkNotificationReadCommand command) {
        if (command == null) {
            throw new InvalidNotificationException(
                    "알림 읽음 처리 command는 null일 수 없습니다."
            );
        }

        if (command.requesterId() ==null || command.requesterId() <= 0) {
            throw new InvalidNotificationException(
                    "requesterId는 1이상이여아 합니다."
            );
        }

        if (command.notificationIds() == null || command.notificationIds().isEmpty()) {
            throw new InvalidNotificationException(
                    "읽음 처리할 알림 ID가 존재하지 않습니다."
            );
        }

        if (command.notificationIds().stream()
                .anyMatch(notificationId ->
                        notificationId == null || notificationId <= 0
                )) {
            throw new InvalidNotificationException(
                    "알림 ID는 1 이상이어야 합니다."
            );
        }

        if (command.notificationIds().size() > 100) {
            throw new InvalidNotificationException(
                    "최대 동시 처리 알림 개수는 100개 입니다."
            );
        }
    }
}
