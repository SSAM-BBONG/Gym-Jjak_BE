package com.ssambbong.gymjjak.notification.domain.model;

import com.ssambbong.gymjjak.notification.domain.exception.InvalidNotificationException;
import com.ssambbong.gymjjak.notification.domain.type.NotificationCategory;
import com.ssambbong.gymjjak.notification.domain.type.NotificationTargetType;
import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Notification {

    // 만료 기간
    private static final long DEFAULT_EXPIRE_DAYS = 7;

    private final Long notificationId;
    private final Long receiverId;
    private final NotificationType type;
    private final NotificationCategory category;
    private final String title;
    private final String content;
    private final NotificationTargetType targetType;
    private final Long targetId;
    private final LocalDateTime eventAt;
    private final LocalDateTime readAt;
    private final LocalDateTime deletedAt;
    private final LocalDateTime expiresAt;

    @Builder(access = AccessLevel.PUBLIC)
    private Notification(
            Long notificationId,
            Long receiverId,
            NotificationType type,
            NotificationCategory category,
            String title,
            String content,
            NotificationTargetType targetType,
            Long targetId,
            LocalDateTime eventAt,
            LocalDateTime readAt,
            LocalDateTime deletedAt,
            LocalDateTime expiresAt
    ) {

        validate(receiverId, type, category, title, content, expiresAt);

        this.notificationId = notificationId;
        this.receiverId = receiverId;
        this.type = type;
        this.category = category;
        this.title = title;
        this.content = content;
        this.targetType = targetType;
        this.targetId = targetId;
        this.eventAt = eventAt;
        this.readAt = readAt;
        this.deletedAt = deletedAt;
        this.expiresAt = expiresAt;
    }

    public static Notification create(
            Long receiverId,
            NotificationType type,
            Long targetId,
            LocalDateTime eventAt
    ) {
        if (type == null) {
            throw new InvalidNotificationException(
                    "알림 타입은 필수입니다."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        return Notification.builder()
                .receiverId(receiverId)
                .type(type)
                .category(type.getCategory())
                .title(type.getTitle())
                .content(type.getContent())
                .targetType(type.getTargetType())
                .targetId(targetId)
                .eventAt(eventAt)
                .readAt(null)
                .deletedAt(null)
                .expiresAt(now.plusDays(DEFAULT_EXPIRE_DAYS))
                .build();
    }

    public static Notification restore(
            Long notificationId,
            Long receiverId,
            NotificationType type,
            NotificationCategory category,
            String title,
            String content,
            NotificationTargetType targetType,
            Long targetId,
            LocalDateTime eventAt,
            LocalDateTime readAt,
            LocalDateTime deletedAt,
            LocalDateTime expiresAt
    ) {
        return Notification.builder()
                .notificationId(notificationId)
                .receiverId(receiverId)
                .type(type)
                .category(category)
                .title(title)
                .content(content)
                .targetType(targetType)
                .targetId(targetId)
                .eventAt(eventAt)
                .readAt(readAt)
                .deletedAt(deletedAt)
                .expiresAt(expiresAt)
                .build();
    }
    // 읽음 표시
    public Notification markAsRead() {
        if (isRead()) {
            return this;
        }

        LocalDateTime now = LocalDateTime.now();

        return Notification.builder()
                .notificationId(notificationId)
                .receiverId(receiverId)
                .type(type)
                .category(category)
                .title(title)
                .content(content)
                .targetType(targetType)
                .targetId(targetId)
                .eventAt(eventAt)
                .readAt(now)
                .deletedAt(deletedAt)
                .expiresAt(expiresAt)
                .build();
    }
    // soft delete
    public Notification delete() {
        if (isDeleted()) {
            return this;
        }

        LocalDateTime now = LocalDateTime.now();

        return Notification.builder()
                .notificationId(notificationId)
                .receiverId(receiverId)
                .type(type)
                .category(category)
                .title(title)
                .content(content)
                .targetType(targetType)
                .targetId(targetId)
                .eventAt(eventAt)
                .readAt(readAt)
                .deletedAt(now)
                .expiresAt(expiresAt)
                .build();
    }

    public boolean isRead() {
        return readAt != null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    // 만료 여부 확인
    public boolean isExpired(LocalDateTime now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }

    // 알림 수신자 본인 검증
    public boolean isOwnedBy(Long requesterId) {
        return receiverId != null && receiverId.equals(requesterId);
    }

    // 필수 값 검증
    private void validate(
            Long receiverId,
            NotificationType type,
            NotificationCategory category,
            String title,
            String content,
            LocalDateTime expiresAt
    ) {
        if (receiverId == null || receiverId <= 0) {
            throw new InvalidNotificationException(
                    "receiverId는 1 이상이어야 합니다."
            );
        }

        if (type == null) {
            throw new InvalidNotificationException(
                    "알림 타입은 필수입니다."
            );
        }

        if (category == null) {
            throw new InvalidNotificationException(
                    "알림 카테고리는 필수입니다."
            );
        }

        if (title == null || title.isBlank()) {
            throw new InvalidNotificationException(
                    "알림 제목은 필수입니다."
            );
        }

        if (content == null || content.isBlank()) {
            throw new InvalidNotificationException(
                    "알림 내용은 필수입니다."
            );
        }

        if (expiresAt == null) {
            throw new InvalidNotificationException(
                    "알림 만료 시각은 필수입니다."
            );
        }
    }
}
