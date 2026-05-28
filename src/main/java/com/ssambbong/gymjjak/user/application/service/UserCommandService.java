package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.user.application.command.LoginCommand;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.application.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.application.result.LoginResult;
import com.ssambbong.gymjjak.user.domain.model.User;
import com.ssambbong.gymjjak.user.domain.policy.UserPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService implements UserCommandUseCase {

    private final UserPort userPort;
    private final UserPolicy userPolicy;

    @Override
    public void registerUser(RegisterUserCommand command) {

        log.debug("[UserRegister] request username={}, nickname={}, phone={}",
                command.username(),
                command.nickname(),
                maskPhone(command.phone())
        );

        userPolicy.validateDuplicateUsername(command.username());
        userPolicy.validateDuplicateNickname(command.nickname());
        userPolicy.validateDuplicatePhone(command.phone());

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

        userPolicy.validateLoginAllowed(user);

        String accessToken = userPort.createAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        String refreshToken = userPort.createRefreshToken(user.getId(), user.getUsername());

        userPort.saveOrUpdateRefreshToken(user.getId(), refreshToken);

        log.info("[UserLoginSuccess] userId={}, username={}, role={}",
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return new LoginResult(
                accessToken,
                refreshToken
        );

    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) {
            return "****";
        }

        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }
}
