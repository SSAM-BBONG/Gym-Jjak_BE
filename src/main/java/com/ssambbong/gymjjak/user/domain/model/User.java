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
        this.username = validateRequired(username, UserErrorCode.USERNAME_REQUIRED);
        this.password = validateRequired(password, UserErrorCode.PASSWORD_REQUIRED);
        this.name = validateRequired(name, UserErrorCode.NAME_REQUIRED);
        this.nickname = validateRequired(nickname, UserErrorCode.NICKNAME_REQUIRED);
        this.phone = validateRequired(phone, UserErrorCode.PHONE_REQUIRED);
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

        this.name = validateRequired(name, UserErrorCode.NAME_REQUIRED);
        this.nickname = validateRequired(nickname, UserErrorCode.NICKNAME_REQUIRED);
        this.phone = validateRequired(phone, UserErrorCode.PHONE_REQUIRED);
        this.updatedAt = updatedAt;
    }

    public void changePassword(String encodedPassword, LocalDateTime  updatedAt) {
        validateUsableUser();

        this.password = validateRequired(encodedPassword, UserErrorCode.PASSWORD_REQUIRED);
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void changeRole(UserRole role, LocalDateTime  updatedAt) {
        validateNotWithdrawn();

        this.role = Objects.requireNonNull(role, "role은 필수입니다.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void markLoggedIn(LocalDateTime  lastLoginAt) {
        validateLoginAllowed();
        this.lastLoginAt = Objects.requireNonNull(lastLoginAt, "lastLoginAt은 필수입니다.");
    }

    public void suspendForSevenDays(LocalDateTime  updatedAt) {
        validateNotWithdrawn();

        if (this.status == UserStatus.DAY_7) {
            throw new UserException(UserErrorCode.USER_ALREADY_SEVEN_DAYS_SUSPENDED);
        }

        this.status = UserStatus.DAY_7;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void suspendPermanently(LocalDateTime  updatedAt) {
        validateNotWithdrawn();

        if (this.status == UserStatus.ETERNAL) {
            throw new UserException(UserErrorCode.USER_ALREADY_PERMANENTLY_SUSPENDED);
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
            throw new UserException(UserErrorCode.USER_ALREADY_WITHDRAWN);
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
        if (isWithdrawn()) {
            throw new UserException(UserErrorCode.LOGIN_FAILED);
        }

        if (this.status != UserStatus.ACTIVE) {
            throw new UserException(UserErrorCode.LOGIN_RESTRICTED);
        }
    }

    public void validateUsableUser() {
        validateNotWithdrawn();

        if (this.status != UserStatus.ACTIVE) {
            throw new UserException(UserErrorCode.USER_NOT_ACTIVE);
        }
    }

    private void validateNotWithdrawn() {
        if (isWithdrawn()) {
            throw new UserException(UserErrorCode.USER_WITHDRAWN);
        }
    }

    private static String validateRequired(String value, UserErrorCode errorCode) {
        if (value == null || value.isBlank()) {
            throw new UserException(errorCode);
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
