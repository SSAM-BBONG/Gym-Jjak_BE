package com.ssambbong.gymjjak.user.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "refresh_token", nullable = false, length = 512)
    private String refreshToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private RefreshTokenJpaEntity(
            Long userId,
            String refreshToken,
            LocalDateTime createdAt
    ) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
    }

    public static RefreshTokenJpaEntity create(
            Long userId,
            String refreshToken,
            LocalDateTime createdAt
    ) {
        return new RefreshTokenJpaEntity(
                userId,
                refreshToken,
                createdAt
        );
    }

    public void updateToken(
            String refreshToken,
            LocalDateTime createdAt
    ) {
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
    }
}
