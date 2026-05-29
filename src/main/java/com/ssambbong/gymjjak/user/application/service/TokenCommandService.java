package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.user.application.command.ReissueTokenCommand;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.in.TokenCommandUsecase;
import com.ssambbong.gymjjak.user.application.port.out.TokenPort;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenCommandService implements TokenCommandUsecase {

    private final TokenPort tokenPort;
    private final UserPort userPort;

    @Override
    public String reissueAccessToken(ReissueTokenCommand command) {
        String refreshToken = command.refreshToken();

        log.info("[토큰 재발급] AccessToken 재발급 요청 시작");

        // 1. refreshToken 자체가 유효한 JWT인지 검증
        if (!tokenPort.validateToken(refreshToken)) {
            log.warn("[토큰 재발급] 유효하지 않은 RefreshToken");
            throw new UserException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. refreshToken에서 userId 추출
        Long userId = tokenPort.getUserId(refreshToken);
        log.info("[토큰 재발급] RefreshToken 검증 성공. userId={}", userId);

        // 3. DB에 저장된 refreshToken 조회
        String savedRefreshToken = tokenPort.findRefreshTokenByUserId(userId)
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
        String accessToken = tokenPort.createAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        log.info("[토큰 재발급] AccessToken 재발급 성공. userId={}, role={}", user.getId(), user.getRole());

        return accessToken;

    }
}
