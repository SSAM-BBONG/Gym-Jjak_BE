package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.domain.model.BlacklistSourceType;
import com.ssambbong.gymjjak.user.domain.model.BlacklistStatus;
import com.ssambbong.gymjjak.user.domain.model.BlacklistType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlacklistsJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "admin_id")
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private BlacklistType type;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BlacklistStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private BlacklistSourceType sourceType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private BlacklistsJpaEntity(
            Long id,
            Long userId,
            Long adminId,
            BlacklistType type,
            String reason,
            LocalDateTime endedAt,
            BlacklistStatus status,
            BlacklistSourceType sourceType,
            LocalDateTime createdAt,
            LocalDateTime deletedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.adminId = adminId;
        this.type = type;
        this.reason = reason;
        this.endedAt = endedAt;
        this.status = status;
        this.sourceType = sourceType;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public static BlacklistsJpaEntity of(
            Long id,
            Long userId,
            Long adminId,
            BlacklistType type,
            String reason,
            LocalDateTime endedAt,
            BlacklistStatus status,
            BlacklistSourceType sourceType,
            LocalDateTime createdAt,
            LocalDateTime deletedAt
    ) {
        return new BlacklistsJpaEntity(
                id,
                userId,
                adminId,
                type,
                reason,
                endedAt,
                status,
                sourceType,
                createdAt,
                deletedAt
        );
    }
}
