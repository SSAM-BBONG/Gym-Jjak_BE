package com.ssambbong.gymjjak.user.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blacklist {

    private Long id;
    private Long userId;
    private Long adminId;
    private BlacklistType type;
    private String reason;
    private LocalDateTime endedAt;
    private BlacklistStatus status;
    private BlacklistSourceType sourceType;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    private Blacklist(
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

    public static Blacklist createByAdmin(
            Long userId,
            Long adminId,
            BlacklistType type,
            String reason,
            LocalDateTime endedAt,
            LocalDateTime now
    ) {
        return new Blacklist(
                null,
                userId,
                adminId,
                type,
                reason,
                endedAt,
                BlacklistStatus.ACTIVE,
                BlacklistSourceType.ADMIN,
                now,
                null
        );
    }

    public static Blacklist of(
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
        return new Blacklist(
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
