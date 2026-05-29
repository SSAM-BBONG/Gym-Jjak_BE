package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.global.security.jwt.JwtTokenProvider;
import com.ssambbong.gymjjak.user.application.port.out.TokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenAdapter implements TokenPort {

    private final JwtTokenProvider jwtTokenProvider;
    private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;

    @Override
    public String createAccessToken(Long userId, String username, String role) {
        return jwtTokenProvider.createAccessToken(userId, username, role);
    }

    @Override
    public String createRefreshToken(Long userId, String username) {
        return jwtTokenProvider.createRefreshToken(userId, username);
    }

    @Override
    public void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        LocalDateTime now = LocalDateTime.now();

        springDataRefreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        existingRefreshToken -> existingRefreshToken.updateToken(refreshToken, now)
                        ,() -> springDataRefreshTokenRepository.save(
                                RefreshTokenJpaEntity.create(userId, refreshToken, now)
                        )
                );
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public Long getUserId(String token) {
        return jwtTokenProvider.getUserId(token);
    }

    @Override
    public Optional<String> findRefreshTokenByUserId(Long userId) {
        return springDataRefreshTokenRepository.findByUserId(userId)
                .map(RefreshTokenJpaEntity::getRefreshToken);
    }
}
