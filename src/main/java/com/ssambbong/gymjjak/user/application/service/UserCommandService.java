package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.user.application.command.LoginCommand;
import com.ssambbong.gymjjak.user.application.command.LogoutCommand;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
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

        log.debug("[UserRegister] request username={}, nickname={}, phone={}",
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

        log.info("[UserRegisterSuccess] username={}, nickname={}",
                command.username(),
                command.nickname()
        );
    }

    @Override
    public LoginResult login(LoginCommand command) {

        log.debug("[UserLogin] request username={}", command.username());

        User user = userPort.findByUsername(command.username())
                .orElseThrow(() -> {
                    log.warn("[UserLoginFailed] reason=user_not_found, username={}", command.username());
                    return new UserException(UserErrorCode.LOGIN_FAILED);
                });

        if (!userPort.matchesPassword(command.password(), user.getPassword())) {
            log.warn("[UserLoginFailed] reason=password_mismatch, username={}, userId={}",
                    user.getUsername(),
                    user.getId());
            throw new UserException(UserErrorCode.LOGIN_FAILED);
        }

        user.markLoggedIn(LocalDateTime.now());

        userPort.updateLastLoginAt(
                user.getId(),
                user.getLastLoginAt()
        );

        user.validateLoginAllowed();

        String accessToken = tokenPort.createAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        String refreshToken = tokenPort.createRefreshToken(user.getId(), user.getUsername());

        tokenPort.saveOrUpdateRefreshToken(user.getId(), refreshToken);

        log.info("[UserLoginSuccess] userId={}, username={}, role={}",
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
        log.info("[로그아웃 요청] userId={}", command.userId());
        tokenPort.deleteRefreshToken(command.userId());
        log.info("[로그아웃 완료] refresh token 삭제 완료. userId={}", command.userId());
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) {
            return "****";
        }

        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }

    public void validateDuplicateUsername(String username) {
        if (userPort.existsByUsername(username)) {
            log.warn("[DuplicateUserRegister] type=username, username={}", username);
            throw new UserException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }

    public void validateDuplicateNickname(String nickname) {
        if (userPort.existsByNickname(nickname)) {
            log.warn("[DuplicateUserRegister] type=nickname, nickname={}", nickname);
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateDuplicatePhone(String phone) {
        if (userPort.existsByPhone(phone)) {
            log.warn("[DuplicateUserRegister] type=phone, phone={}", maskPhone(phone));
            throw new UserException(UserErrorCode.DUPLICATE_PHONE);
        }
    }
}
