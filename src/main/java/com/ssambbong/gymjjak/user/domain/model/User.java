package com.ssambbong.gymjjak.user.domain.model;

import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import java.time.LocalDateTime;
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
    private boolean onboardingCompleted;
    private LocalDateTime  lastLoginAt;
    private final LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
    private LocalDateTime  deletedAt;

    private User(
            Long id,
            String username,
            String password,
            String name,
            String nickname,
            String phone,
            UserRole role,
            UserStatus status,
            boolean onboardingCompleted,
            LocalDateTime  lastLoginAt,
            LocalDateTime  createdAt,
            LocalDateTime  updatedAt,
            LocalDateTime  deletedAt
    ) {
        this.id = id;
        this.username = validateRequired(username, "username");
        this.password = validateRequired(password, "password");
        this.name = validateRequired(name, "name");
        this.nickname = validateRequired(nickname, "nickname");
        this.phone = validateRequired(phone, "phone");
        this.role = Objects.requireNonNull(role, "role은 필수입니다.");
        this.status = Objects.requireNonNull(status, "status는 필수입니다.");
        this.onboardingCompleted = onboardingCompleted;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static User register(
            String username,
            String encodedPassword,
            String name,
            String nickname,
            String phone
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
                false,
                null,
                null,
                null,
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
            boolean onboardingCompleted,
            LocalDateTime  lastLoginAt,
            LocalDateTime  createdAt,
            LocalDateTime  updatedAt,
            LocalDateTime  deletedAt
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
                onboardingCompleted,
                lastLoginAt,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public void completeOnboarding() {
        if (this.onboardingCompleted) {
            throw new UserException(UserErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }

        this.onboardingCompleted = true;
    }

    public void updateProfile(
            String name,
            String nickname,
            String phone,
            LocalDateTime  updatedAt
    ) {
        validateUsableUser();

        this.name = validateRequired(name, "name");
        this.nickname = validateRequired(nickname, "nickname");
        this.phone = validateRequired(phone, "phone");
        this.updatedAt = updatedAt;
    }

    public void changePassword(String encodedPassword, LocalDateTime  updatedAt) {
        validateUsableUser();

        this.password = validateRequired(encodedPassword, "password");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void changeRole(UserRole role, LocalDateTime  updatedAt) {
        validateNotWithdrawn();

        this.role = Objects.requireNonNull(role, "role은 필수입니다.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void markLoggedIn(LocalDateTime  lastLoginAt) {
        this.lastLoginAt = Objects.requireNonNull(lastLoginAt, "lastLoginAt은 필수입니다.");
    }

    public void suspendForSevenDays(LocalDateTime  updatedAt) {
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

    public void suspendPermanently(LocalDateTime  updatedAt) {
        validateNotWithdrawn();

        if (this.status == UserStatus.ETERNAL) {
            throw new IllegalStateException("이미 영구 정지된 회원입니다.");
        }

        this.status = UserStatus.ETERNAL;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void activate(LocalDateTime  updatedAt) {
        validateNotWithdrawn();

        this.status = UserStatus.ACTIVE;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void withdraw(LocalDateTime  deletedAt) {
        if (isWithdrawn()) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }

        this.deletedAt = Objects.requireNonNull(deletedAt, "deletedAt은 필수입니다.");
        this.updatedAt = deletedAt;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE && !isWithdrawn();
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
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

    public void validateLoginAllowed() {
        if (this.status != UserStatus.ACTIVE) {
            throw new UserException(UserErrorCode.LOGIN_RESTRICTED);
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

    public LocalDateTime  getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime  getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime  getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime  getDeletedAt() {
        return deletedAt;
    }
}
