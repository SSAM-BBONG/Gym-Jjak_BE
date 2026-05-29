package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_nickname", columnNames = "nickname"),
                @UniqueConstraint(name = "uk_users_phone", columnNames = "phone")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserStatus status;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted;

    @Column(name = "last_login_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime  lastLoginAt;

    public UserJpaEntity(
            Long id,
            String username,
            String password,
            String name,
            String nickname,
            String phone,
            UserRole role,
            UserStatus status,
            boolean onboardingCompleted,
            LocalDateTime  lastLoginAt
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.onboardingCompleted = onboardingCompleted;
        this.lastLoginAt = lastLoginAt;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void completeOnboarding() {
        this.onboardingCompleted = true;
    }
}
