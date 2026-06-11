package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.user.application.command.LoginCommand;
import com.ssambbong.gymjjak.user.application.command.LogoutCommand;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.command.UpdateProfileCommand;
import com.ssambbong.gymjjak.user.application.result.UserProfileResult;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import com.ssambbong.gymjjak.user.application.port.out.TokenPort;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.application.result.LoginResult;
import com.ssambbong.gymjjak.user.domain.model.User;
import com.ssambbong.gymjjak.user.domain.policy.UserPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService implements UserCommandUseCase {

    private final UserPort userPort;
    private final TokenPort tokenPort;

    @Override
    public void registerUser(RegisterUserCommand command) {

        log.debug("event=user_register_start username={}, nickname={}, phone={}",
                command.username(),
                command.nickname(),
                maskPhone(command.phone())
        );

        UserPolicy.validatePasswordPolicy(command.password());
        validateDuplicateUsername(command.username());
        validateDuplicateNickname(command.nickname());
        validateDuplicatePhone(command.phone());

        String encodedPassword = userPort.encode(command.password());

        User user = User.register(
                command.username(),
                encodedPassword,
                command.name(),
                command.nickname(),
                command.phone()
        );

        userPort.save(user);

        log.info("event=user_register_succeed username={}, nickname={}",
                command.username(),
                command.nickname()
        );
    }

    @Override
    public LoginResult login(LoginCommand command) {

        log.debug("event=user_login_start username={}", command.username());

        User user = userPort.findByUsername(command.username())
                .orElseThrow(() -> {
                    return new UserException(UserErrorCode.LOGIN_FAILED);
                });

        if (!userPort.matchesPassword(command.password(), user.getPassword())) {
            throw new UserException(UserErrorCode.LOGIN_FAILED);
        }

        user.markLoggedIn(LocalDateTime.now());

        user.validateLoginAllowed();

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
        if (userPort.existsByPhone(phone)) {
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
}
