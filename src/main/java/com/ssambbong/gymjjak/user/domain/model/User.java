package com.ssambbong.gymjjak.user.domain.model;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Objects;

public class User {

    private final Long id;
    private final String username;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private Instant lastLoginAt;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private User(
            Long id,
            String username,
            String password,
            String name,
            String nickname,
            String phone,
            UserRole role,
            UserStatus status,
            Instant lastLoginAt,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this.id = id;
        this.username = validateRequired(username, "username");
        this.password = validateRequired(password, "password");
        this.name = validateRequired(name, "name");
        this.nickname = validateRequired(nickname, "nickname");
        this.phone = validateRequired(phone, "phone");
        this.role = Objects.requireNonNull(role, "role은 필수입니다.");
        this.status = Objects.requireNonNull(status, "status는 필수입니다.");
        this.lastLoginAt = lastLoginAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt은 필수입니다.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
        this.deletedAt = deletedAt;
    }

    public static User register(
            String username,
            String encodedPassword,
            String name,
            String nickname,
            String phone,
            Instant createdAt
    ) {
        return new User(
                null,
                username,
                encodedPassword,
                name,
                nickname,
                phone,
                UserRole.USER,
                UserStatus.ACTIVE,
                null,
                createdAt,
                createdAt,
                null
        );
    }

    public static User reconstruct(
            Long id,
            String username,
            String password,
            String name,
            String nickname,
            String phone,
            UserRole role,
            UserStatus status,
            Instant lastLoginAt,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        return new User(
                id,
                username,
                password,
                name,
                nickname,
                phone,
                role,
                status,
                lastLoginAt,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public void updateProfile(
            String name,
            String nickname,
            String phone,
            Instant updatedAt
    ) {
        validateUsableUser();

        this.name = validateRequired(name, "name");
        this.nickname = validateRequired(nickname, "nickname");
        this.phone = validateRequired(phone, "phone");
        this.updatedAt = updatedAt;
    }

    public void changePassword(String encodedPassword, Instant updatedAt) {
        validateUsableUser();

        this.password = validateRequired(encodedPassword, "password");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void changeRole(UserRole role, Instant updatedAt) {
        validateNotWithdrawn();

        this.role = Objects.requireNonNull(role, "role은 필수입니다.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void markLoggedIn(Instant lastLoginAt) {
        validateLoginAllowed();

        this.lastLoginAt = Objects.requireNonNull(lastLoginAt, "lastLoginAt은 필수입니다.");
    }

    public void suspendForSevenDays(Instant updatedAt) {
        validateNotWithdrawn();

        if (this.status == UserStatus.DAY_7) {
            throw new IllegalStateException("이미 7일 정지된 회원입니다.");
        }

        if (this.status == UserStatus.ETERNAL) {
            throw new IllegalStateException("영구 정지된 회원은 7일 정지로 변경할 수 없습니다.");
        }

        this.status = UserStatus.DAY_7;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void suspendPermanently(Instant updatedAt) {
        validateNotWithdrawn();

        if (this.status == UserStatus.ETERNAL) {
            throw new IllegalStateException("이미 영구 정지된 회원입니다.");
        }

        this.status = UserStatus.ETERNAL;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void activate(Instant updatedAt) {
        validateNotWithdrawn();

        this.status = UserStatus.ACTIVE;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void withdraw(Instant deletedAt) {
        if (isWithdrawn()) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }

        this.deletedAt = Objects.requireNonNull(deletedAt, "deletedAt은 필수입니다.");
        this.updatedAt = deletedAt;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE && !isWithdrawn();
    }

    public boolean isSevenDaysSuspended() {
        return this.status == UserStatus.DAY_7 && !isWithdrawn();
    }

    public boolean isPermanentlySuspended() {
        return this.status == UserStatus.ETERNAL && !isWithdrawn();
    }

    public boolean isWithdrawn() {
        return this.deletedAt != null;
    }

    private void validateLoginAllowed() {
        validateNotWithdrawn();

        if (this.status == UserStatus.DAY_7) {
            throw new IllegalStateException("7일 정지된 회원은 로그인할 수 없습니다.");
        }

        if (this.status == UserStatus.ETERNAL) {
            throw new IllegalStateException("영구 정지된 회원은 로그인할 수 없습니다.");
        }
    }

    private void validateUsableUser() {
        validateNotWithdrawn();

        if (this.status != UserStatus.ACTIVE) {
            throw new IllegalStateException("정상 상태의 회원만 사용할 수 있습니다.");
        }
    }

    private void validateNotWithdrawn() {
        if (isWithdrawn()) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }
    }

    private static String validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }

        return value;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
