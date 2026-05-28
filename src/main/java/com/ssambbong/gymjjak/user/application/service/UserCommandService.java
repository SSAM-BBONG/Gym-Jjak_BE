package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.user.application.command.LoginCommand;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.command.ReissueTokenCommand;
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

        user.markLoggedIn(LocalDateTime.now());

        userPort.updateLastLoginAt(
                user.getId(),
                user.getLastLoginAt()
        );

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

    @Override
    public String reissueAccessToken(ReissueTokenCommand command) {
        String refreshToken = command.refreshToken();

        log.info("[토큰 재발급] AccessToken 재발급 요청 시작");

        // 1. refreshToken 자체가 유효한 JWT인지 검증
        if (!userPort.validateToken(refreshToken)) {
            log.warn("[토큰 재발급] 유효하지 않은 RefreshToken");
            throw new UserException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. refreshToken에서 userId 추출
        Long userId = userPort.getUserId(refreshToken);
        log.info("[토큰 재발급] RefreshToken 검증 성공. userId={}", userId);

        // 3. DB에 저장된 refreshToken 조회
        String savedRefreshToken = userPort.findRefreshTokenByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("[토큰 재발급] DB에 저장된 RefreshToken 없음. userId={}", userId);
                    return new UserException(UserErrorCode.REFRESH_TOKEN_NOT_FOUND);
                });

        // 4. 요청으로 들어온 refreshToken과 DB refreshToken 비교
        if (!savedRefreshToken.equals(refreshToken)) {
            log.warn("[토큰 재발급] RefreshToken 불일치. userId={}", userId);
            throw new UserException(UserErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 5. 사용자 조회
        User user = userPort.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 6. 새 accessToken 발급
        String accessToken = userPort.createAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        log.info("[토큰 재발급] AccessToken 재발급 성공. userId={}, role={}", user.getId(), user.getRole());

        return accessToken;

    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) {
            return "****";
        }

        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }
}
