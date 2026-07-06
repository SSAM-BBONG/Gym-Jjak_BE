package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.user.application.command.*;
import com.ssambbong.gymjjak.user.application.port.out.BlacklistPort;
import com.ssambbong.gymjjak.user.application.result.*;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import com.ssambbong.gymjjak.user.application.port.out.TokenPort;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.domain.model.*;
import com.ssambbong.gymjjak.user.domain.policy.UserPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService implements UserCommandUseCase {

    private final UserPort userPort;
    private final TokenPort tokenPort;
    private final BlacklistPort blacklistPort;

    @Override
    public void registerUser(RegisterUserCommand command) {

        String username = normalize(command.username());
        String name = normalize(command.name());
        String nickname = normalize(command.nickname());
        String phone = normalize(command.phone());

        log.debug("event=user_register_start username={}, nickname={}, phone={}",
                username,
                nickname,
                maskPhone(phone)
        );

        UserPolicy.validatePasswordPolicy(command.password());
        validateDuplicateUsername(username);
        validateDuplicateNickname(nickname);
        validateDuplicatePhone(phone);

        String encodedPassword = userPort.encode(command.password());

        User user = User.register(
                username,
                encodedPassword,
                name,
                nickname,
                phone
        );

        userPort.save(user);

        log.info("event=user_register_succeed username={}, nickname={}, phone={}",
                username,
                nickname,
                maskPhone(phone)
        );
    }

    @Override
    public LoginResult login(LoginCommand command) {

        log.debug("event=user_login_start username={}", command.username());

        User user = userPort.findByUsername(command.username())
                .orElseThrow(() -> {
                    return new UserException(UserErrorCode.LOGIN_FAILED);
                });

        LocalDateTime now = LocalDateTime.now();

        UserStatus beforeStatus = user.getStatus();

        user.releaseSuspensionIfExpired(now);

        if (user.getPassword() == null) {
            log.warn("event=user_login_failed reason=social_login_required userId={}, username={}, provider={}",
                    user.getId(),
                    user.getUsername(),
                    user.getSocialProvider()
            );

            throw new UserException(UserErrorCode.SOCIAL_LOGIN_REQUIRED);
        }

        if (!userPort.matchesPassword(command.password(), user.getPassword())) {
            throw new UserException(UserErrorCode.LOGIN_FAILED);
        }

        if (beforeStatus != user.getStatus()) {
            userPort.save(user);
        }

        user.validateLoginAllowed();

        user.markLoggedIn(now);

        userPort.updateLastLoginAt(
                user.getId(),
                user.getLastLoginAt()
        );

        String accessToken = tokenPort.createAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        String refreshToken = tokenPort.createRefreshToken(user.getId(), user.getUsername());

        tokenPort.saveOrUpdateRefreshToken(user.getId(), refreshToken);

        log.info("event=user_login_succeed userId={}, username={}, role={}",
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return new LoginResult(
                accessToken,
                refreshToken,
                user.getRole().name(),
                user.isOnboardingCompleted()
        );

    }

    @Override
    public void logout(LogoutCommand command) {
        log.debug("event=user_logout_start userId={}", command.userId());
        tokenPort.deleteRefreshToken(command.userId());
        log.info("event=refreshToken_delete_succeed userId={}", command.userId());
        log.info("event=user_logout_succeed userId={}", command.userId());
    }

    @Override
    public void verifyPassword(Long userId, String rawPassword) {
        log.debug("event=user_verifyPassword_start userId={}", userId);
        User user = userPort.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (!userPort.matchesPassword(rawPassword, user.getPassword())) {
            throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
        }
        log.info("event=user_verifyPassword_succeed userId={}", userId);
    }

    @Override
    public UserProfileResult findMyProfileInfo(Long userId) {
        log.debug("event=user_findProfile_start userId={}", userId);
        User user = userPort.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.validateUsableUser();

        log.info("event=user_findProfile_succeed userId={}", userId);

        return UserProfileResult.from(user);
    }

    @Override
    public void updateProfile(UpdateProfileCommand command) {
        log.debug("event=user_updateProfile_start userId={}", command.userId());
        User user = userPort.findById(command.userId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        validateDuplicateNicknameExceptforMe(command.nickname(), command.userId());
        validateDuplicatePhoneExceptforMe(command.phone(), command.userId());

        user.updateProfile(
                command.name(),
                command.nickname(),
                command.phone(),
                LocalDateTime.now()
        );
        userPort.save(user);
        log.info("event=user_updateProfile_succeed userId={}", command.userId());

    }

    @Override
    public void withdrawUser(Long userId) {
        log.debug("event=user_withdrawUser_start userId={}", userId);

        User user = userPort.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (user.isWithdrawn()) {
            throw new UserException(UserErrorCode.USER_ALREADY_WITHDRAWN);
        }

        userPort.withdraw(userId, LocalDateTime.now());

        tokenPort.deleteRefreshToken(user.getId());

        log.info("event=user_withdrawUser_succeed userId={}", userId);
    }

    @Override
    public void updateUserStatus(UpdateUserStatusCommand command) {
        log.debug("event=user_statusUpdate_start userId={} status={}", command.userId(), command.status());

        validateStatusChangeReason(command.status(), command.reason());

        User user = userPort.findById(command.userId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        blacklistPort.releaseActiveBlacklistsByUserId(user.getId());

        if (command.status() == UserStatus.ACTIVE) {
            user.changeStatus(UserStatus.ACTIVE);
            userPort.updateStatus(user.getId(), UserStatus.ACTIVE);
            return;
        }
        BlacklistType blacklistType = toBlacklistType(command.status());
        LocalDateTime endedAt = calculateEndedAt(command.status(), now);

        Blacklist blacklist = Blacklist.createByAdmin(
                user.getId(),
                command.adminId(),
                blacklistType,
                command.reason(),
                endedAt,
                now
        );

        blacklistPort.save(blacklist);

        user.changeStatus(command.status());
        userPort.updateStatus(user.getId(), user.getStatus());

        log.info("event=user_statusUpdate_succeed userId={} status={}", command.userId(), command.status());
    }

    @Override
    public void updatePassword(UpdatePasswordCommand command) {
        if (command.newPassword() == null || command.newPassword().isBlank()
                || command.checkNewPassword() == null || command.checkNewPassword().isBlank()) {
            throw new UserException(UserErrorCode.PASSWORD_CONFIRM_NOT_MATCHED);
        }
        UserPolicy.validatePasswordPolicy(command.newPassword());

        log.debug("event=password_update_start userId={}", command.userId());

        if (!command.newPassword().equals(command.checkNewPassword())) {
            throw new UserException(UserErrorCode.PASSWORD_CONFIRM_NOT_MATCHED);
        }
        User user = userPort.findById(command.userId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (userPort.matchesPassword(command.newPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.SAME_AS_OLD_PASSWORD);
        }

        String encodedPassword = userPort.encode(command.newPassword());

        userPort.updatePassword(user.getId(), encodedPassword, LocalDateTime.now());
        log.info("event=password_update_succeed userId={}", command.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<FindUserResult> findUsers(String keyword, int page, int size) {
        log.debug("event=users_find_start, keyword={}, page={}, size={}", keyword, page, size);

        PageResult<FindUserResult> result = userPort.findUsers(keyword, page, size);

        log.info(
                "event=users_find_succeed, keyword={}, page={}, size={}, resultCount={}, totalElements={}, totalPages={}, hasNext={}",
                keyword,
                page,
                size,
                result.content().size(),
                result.totalElements(),
                result.totalPages(),
                result.hasNext()
        );

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<FindBlacklistUserResult> findBlacklistUsers(String keyword, int page, int size) {
        log.debug(
                "event=users_findBlacklistUsers_start, keyword={}, page={}, size={}",
                keyword,
                page,
                size
        );

        PageResult<FindBlacklistUserResult> result =
                userPort.findBlacklistUsers(keyword, page, size);

        log.info(
                "event=users_findBlacklistUsers_succeed, keyword={}, page={}, size={}, resultCount={}, totalElements={}, totalPages={}, hasNext={}",
                keyword,
                page,
                size,
                result.content().size(),
                result.totalElements(),
                result.totalPages(),
                result.hasNext()
        );

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<FindTrainerUserResult> findTrainerUsers(String keyword, int page, int size) {
        log.debug(
                "event=users_findTrainerUsers_start, keyword={}, page={}, size={}",
                keyword,
                page,
                size
        );

        PageResult<FindTrainerUserResult> result = userPort.findTrainerUsers(keyword, page, size);

        log.info(
                "event=users_findTrainerUsers_succeed, keyword={}, page={}, size={}, resultCount={}, totalElements={}, totalPages={}, hasNext={}",
                keyword,
                page,
                size,
                result.content().size(),
                result.totalElements(),
                result.totalPages(),
                result.hasNext()
        );

        return result;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) {
            return "****";
        }

        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }

    public void validateDuplicateUsername(String username) {
        if (userPort.existsByUsername(username)) {
            throw new UserException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }

    public void validateDuplicateNickname(String nickname) {
        if (userPort.existsByNickname(nickname)) {
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateDuplicatePhone(String phone) {
        if (userPort.existsByPhoneAndRole(phone, UserRole.USER)) {
            throw new UserException(UserErrorCode.DUPLICATE_PHONE);
        }
    }

    private void validateDuplicateNicknameExceptforMe(String nickname, Long userId) {
        if (userPort.existsByNicknameAndIdNot(nickname, userId)) {
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validateDuplicatePhoneExceptforMe(String phone, Long userId) {
        if (userPort.existsByPhoneAndIdNot(phone, userId)) {
            throw new UserException(UserErrorCode.DUPLICATE_PHONE);
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private void validateStatusChangeReason(UserStatus status, String reason) {
        if (status == UserStatus.DAY_7 || status == UserStatus.ETERNAL) {
            if (reason == null || reason.isBlank()) {
                throw new UserException(UserErrorCode.USER_STATUS_REASON_REQUIRED);
            }
        }
    }

    private BlacklistType toBlacklistType(UserStatus status) {
        if (status == UserStatus.DAY_7) {
            return BlacklistType.DAY_7;
        }

        if (status == UserStatus.ETERNAL) {
            return BlacklistType.ETERNAL;
        }

        throw new UserException(UserErrorCode.INVALID_USER_STATUS);
    }

    private LocalDateTime calculateEndedAt(UserStatus status, LocalDateTime now) {
        if (status == UserStatus.DAY_7) {
            return now.plusDays(7);
        }

        if (status == UserStatus.ETERNAL) {
            return null;
        }

        return null;
    }

    @Override
    public void completeSocialSignup(CompleteSocialSignupCommand command) {

        String nickname = normalize(command.nickname());
        String phone = normalize(command.phone());
        if (nickname == null || nickname.isBlank()) {
            throw new UserException(UserErrorCode.NICKNAME_REQUIRED);
        }
        if (phone == null || phone.isBlank()) {
            throw new UserException(UserErrorCode.PHONE_REQUIRED);
        }

        User user = userPort.findById(command.userId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (userPort.existsByNickname(nickname)) {
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        if (userPort.existsByPhoneAndRole(phone, UserRole.USER)) {
            throw new UserException(UserErrorCode.DUPLICATE_PHONE);
        }

        user.completeSocialSignup(
                nickname,
                phone,
                LocalDateTime.now()
        );

        userPort.save(user);
    }
}
