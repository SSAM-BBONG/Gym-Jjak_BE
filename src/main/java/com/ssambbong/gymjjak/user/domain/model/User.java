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
    private SocialProvider socialProvider;
    private String socialId;
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
            SocialProvider socialProvider,
            String socialId,
            LocalDateTime  lastLoginAt,
            LocalDateTime  createdAt,
            LocalDateTime  updatedAt,
            LocalDateTime  deletedAt
    ) {
        this.id = id;
        this.username = normalizeRequiredText(username, UserErrorCode.USERNAME_REQUIRED);
        this.password = normalizeNullableText(password);
        this.name = normalizeRequiredText(name, UserErrorCode.NAME_REQUIRED);
        this.nickname = normalizeNullableText(nickname);
        this.phone =  normalizeNullableText(phone);
        this.role = Objects.requireNonNull(role, "role은 필수입니다.");
        this.status = Objects.requireNonNull(status, "status는 필수입니다.");
        this.onboardingCompleted = onboardingCompleted;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
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
                null,
                null,
                null
        );
    }

    public static User registerSocial(
            String username,
            String name,
            SocialProvider socialProvider,
            String socialId,
            LocalDateTime now
    ) {
        return new User(
                null,
                username,
                null,
                name,
                null,
                null,
                UserRole.USER,
                UserStatus.ACTIVE,
                false,
                Objects.requireNonNull(socialProvider, "socialProvider는 필수입니다."),
                validateRequired(socialId, UserErrorCode.SOCIAL_ID_REQUIRED),
                null,
                now,
                now,
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
            SocialProvider socialProvider,
            String socialId,
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
                socialProvider,
                socialId,
                lastLoginAt,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public void completeSocialSignup(
            String nickname,
            String phone,
            LocalDateTime updatedAt
    ) {
        validateUsableUser();

        if (!isSocialUser()) {
            throw new UserException(UserErrorCode.NOT_SOCIAL_USER);
        }

        if (isSocialSignupCompleted()) {
            throw new UserException(UserErrorCode.SOCIAL_SIGNUP_ALREADY_COMPLETED);
        }

        this.nickname = normalizeRequiredText(nickname, UserErrorCode.NICKNAME_REQUIRED);
        this.phone = normalizeRequiredText(phone, UserErrorCode.PHONE_REQUIRED);
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public boolean isSocialUser() {
        return this.socialProvider != null && hasText(this.socialId);
    }

    public boolean isSocialSignupCompleted() {
        if (!isSocialUser()) {
            return true;
        }

        return hasText(this.nickname) && hasText(this.phone);
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

    public void changePassword(String encodedPassword, LocalDateTime updatedAt) {
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

    public void activate(LocalDateTime  updatedAt) {
        validateNotWithdrawn();

        this.status = UserStatus.ACTIVE;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void suspendForSevenDays(LocalDateTime  now) {
        validateNotWithdrawn();

        if (this.status == UserStatus.DAY_7) {
            throw new UserException(UserErrorCode.USER_ALREADY_SEVEN_DAYS_SUSPENDED);
        }

        this.status = UserStatus.DAY_7;
        this.updatedAt = now;
    }

    public void suspendPermanently(LocalDateTime  updatedAt) {
        validateNotWithdrawn();

        if (this.status == UserStatus.ETERNAL) {
            throw new UserException(UserErrorCode.USER_ALREADY_PERMANENTLY_SUSPENDED);
        }

        this.status = UserStatus.ETERNAL;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt은 필수입니다.");
    }

    public void releaseSuspensionIfExpired(LocalDateTime now) {
        if (this.status != UserStatus.DAY_7) {
            return;
        }

        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE && !isWithdrawn();

    }

    public static User createOrganizationAccount(
            String username,
            String encodedPassword,
            String name,
            String nickname,
            String phone,
            LocalDateTime now
    ) {
        LocalDateTime createdTime = Objects.requireNonNull(now, "now는 필수입니다.");

        return new User(
                null,
                normalizeRequiredText(username, UserErrorCode.USERNAME_REQUIRED),
                normalizeRequiredText(encodedPassword, UserErrorCode.PASSWORD_REQUIRED),
                normalizeRequiredText(name, UserErrorCode.NAME_REQUIRED),
                normalizeRequiredText(nickname, UserErrorCode.NICKNAME_REQUIRED),
                normalizeRequiredText(phone, UserErrorCode.PHONE_REQUIRED),
                UserRole.ORGANIZATION,
                UserStatus.ACTIVE,
                true,
                null,
                null,
                null,
                createdTime,
                createdTime,
                null
        );
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

    private static String normalizeRequiredText(String value, UserErrorCode errorCode) {
        String validated = validateRequired(value, errorCode);
        return validated.trim();
    }

    private static String normalizeNullableText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }


    public void changeStatus(UserStatus status) {
        this.status = status;
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

    public SocialProvider getSocialProvider() {
        return socialProvider;
    }

    public String getSocialId() {
        return socialId;
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
