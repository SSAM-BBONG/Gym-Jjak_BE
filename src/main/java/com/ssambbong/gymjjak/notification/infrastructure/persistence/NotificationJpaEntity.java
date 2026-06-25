package com.ssambbong.gymjjak.notification.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import com.ssambbong.gymjjak.notification.domain.type.NotificationCategory;
import com.ssambbong.gymjjak.notification.domain.type.NotificationTargetType;
import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private NotificationCategory category;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 50)
    private NotificationTargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "event_at")
    private LocalDateTime eventAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Builder
    public NotificationJpaEntity(
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
}
