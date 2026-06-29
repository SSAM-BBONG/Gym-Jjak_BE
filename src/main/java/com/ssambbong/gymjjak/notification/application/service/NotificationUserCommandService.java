package com.ssambbong.gymjjak.notification.application.service;

import com.ssambbong.gymjjak.notification.application.command.DeleteNotificationsCommand;
import com.ssambbong.gymjjak.notification.application.command.MarkNotificationReadCommand;
import com.ssambbong.gymjjak.notification.application.result.DeleteNotificationsResult;
import com.ssambbong.gymjjak.notification.application.result.MarkNotificationReadResult;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationUserCommandUseCase;
import com.ssambbong.gymjjak.notification.domain.exception.ForbiddenNotificationAccessException;
import com.ssambbong.gymjjak.notification.domain.exception.InvalidNotificationException;
import com.ssambbong.gymjjak.notification.domain.exception.NotificationNotFoundException;
import com.ssambbong.gymjjak.notification.domain.model.Notification;
import com.ssambbong.gymjjak.notification.domain.repository.NotificationRepository;
import com.ssambbong.gymjjak.notification.infrastructure.metrics.NotificationMetric;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationUserCommandService implements NotificationUserCommandUseCase {

    private final NotificationRepository notificationRepository;

    private final NotificationMetric notificationMetric;

    @Override
    public MarkNotificationReadResult readNotifications(MarkNotificationReadCommand command) {
        // command 검증
        validateReadCommand(command);

        log.info(
                "event=notification_read_started, requesterId={}, requestedCount={}",
                command.requesterId(),
                command.notificationIds().size()
        );

        Timer.Sample readTimer =
                notificationMetric.startTimer();

        String outcome = notificationMetric.success();

        int requestedCount = command.notificationIds().size();
        int processedCount = 0;

        try {
            // 중복 제거 후 정렬
            List<Long> notificationIds = removeDuplicateIds(
                    command.notificationIds()
            );

            // 배치 저장
            Map<Long, Notification> notificationMap =
                    findNotificationsByIds(notificationIds);

            LocalDateTime now = LocalDateTime.now();

            List<Long> readNotificationIds = new ArrayList<>();
            List<Notification> readNotifications = new ArrayList<>();

            for (Long notificationId : notificationIds) {
                // 하나씩 추출
                Notification notification = getNotificationOrThrow(
                        notificationMap,
                        notificationId
                );

                // 본인 알림 검증
                validateAccessibleNotification(
                        notification,
                        command.requesterId(),
                        now
                );

                // 읽기 처리
                if (!notification.isRead()) {
                    readNotifications.add(notification.markAsRead());
                }

                // list에 저장
                readNotificationIds.add(notificationId);
            }

            // 한 번에 저장하기
            if (!readNotifications.isEmpty()) {
                notificationRepository.saveAll(readNotifications);
            }
            
            // 처리된 개수
            processedCount = readNotificationIds.size();

            log.info(
                    "event=notification_read_succeeded, requesterId={}, requestedCount={}, processedCount={}",
                    command.requesterId(),
                    command.notificationIds().size(),
                    processedCount
            );

            return MarkNotificationReadResult.builder()
                    .readNotificationIds(readNotificationIds)
                    .build();
        } catch (RuntimeException exception) {
            outcome = notificationMetric.failure();
            throw exception;
        } finally {
            notificationMetric.recordCommandDuration(
                    readTimer,
                    "read",
                    outcome
            );
            // 요청한 알림 개수 분포
            notificationMetric.recordReadRequestedItems(requestedCount);
            // 처리된 알림 개수 분포
            notificationMetric.recordReadProcessedItems(processedCount);
        }

    }

    @Override
    public DeleteNotificationsResult deleteNotifications(DeleteNotificationsCommand command) {

        validateDeleteCommand(command);

        log.info(
                "event=notification_delete_started, requesterId={}, requestedCount={}",
                command.requesterId(),
                command.notificationIds().size()
        );

        Timer.Sample deleteTimer =
                notificationMetric.startTimer();

        String outcome = notificationMetric.success();

        int requestedCount = command.notificationIds().size();
        int processedCount = 0;

        try {
            // 중복 제거
            List<Long> notificationIds = removeDuplicateIds(
                    command.notificationIds()
            );

            Map<Long, Notification> notificationMap =
                    findNotificationsByIds(notificationIds);

            LocalDateTime now = LocalDateTime.now();

            List<Long> deletedNotificationIds = new ArrayList<>();
            List<Notification> deletedNotifications = new ArrayList<>();

            for (Long notificationId : notificationIds) {
                Notification notification = getNotificationOrThrow(
                        notificationMap,
                        notificationId
                );

                validateAccessibleNotification(
                        notification,
                        command.requesterId(),
                        now
                );

                // 삭제된 알림을 List에 추가
                deletedNotifications.add(notification.delete());
                // 해당 Id값 List에 추가
                deletedNotificationIds.add(notificationId);
            }
            // 모두 DB 저장
            if (!deletedNotifications.isEmpty()) {
                notificationRepository.saveAll(deletedNotifications);
            }

            // 처리된 삭제 알림 개수
            processedCount = deletedNotificationIds.size();

            log.info(
                    "event=notification_delete_succeeded, requesterId={}, requestedCount={}, processedCount={}",
                    command.requesterId(),
                    command.notificationIds().size(),
                    processedCount
            );

            return DeleteNotificationsResult.builder()
                    .deletedNotificationIds(deletedNotificationIds)
                    .build();
        } catch (RuntimeException exception) {
            outcome = notificationMetric.failure();
            throw exception;
        } finally {
            // 삭제 요청 처리 시간 측정
            notificationMetric.recordCommandDuration(
                    deleteTimer,
                    "delete",
                    outcome
            );
            // 삭제 요청 개수 분포
            notificationMetric.recordDeleteRequestedItems(requestedCount);
            // 삭제 처리 개수 분포
            notificationMetric.recordDeleteProcessedItems(processedCount);
        }



    }

    // ========== 공통 내부 메서드 =============
    private Map<Long, Notification> findNotificationsByIds(
            List<Long> notificationIds
    ) {
        return notificationRepository.findAllById(notificationIds)
                .stream()
                .collect(Collectors.toMap(
                        Notification::getNotificationId,
                        Function.identity()
                ));
    }

    private Notification getNotificationOrThrow(
            Map<Long, Notification> notificationMap,
            Long notificationId
    ) {
        Notification notification = notificationMap.get(notificationId);

        if (notification == null) {
            throw new NotificationNotFoundException(notificationId);
        }

        return notification;
    }

    private void validateAccessibleNotification(
            Notification notification,
            Long requesterId,
            LocalDateTime now
    ) {
        if (!notification.isOwnedBy(requesterId)) {
            throw new ForbiddenNotificationAccessException(
                    requesterId,
                    notification.getNotificationId()
            );
        }

        if (notification.isDeleted() || notification.isExpired(now)) {
            throw new NotificationNotFoundException(
                    notification.getNotificationId()
            );
        }
    }

    // ======= 알림 삭제 내부 메서드 ======
    private void validateDeleteCommand(DeleteNotificationsCommand command) {
        if (command == null) {
            throw new InvalidNotificationException(
                    "알림 삭제 command는 필수입니다."
            );
        }

        if (command.requesterId() == null || command.requesterId() <= 0) {
            throw new InvalidNotificationException(
                    "requesterId는 1 이상이어야 합니다."
            );
        }

        if (command.notificationIds() == null || command.notificationIds().isEmpty()) {
            throw new InvalidNotificationException(
                    "삭제할 알림 ID는 필수입니다."
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
                    "알림은 한 번에 최대 100개까지 삭제할 수 있습니다."
            );
        }
    }

    // ======== 알림 읽기 내부 메서드 =============
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
