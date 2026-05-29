package com.ssambbong.gymjjak.user.application.port.out;

import java.util.Optional;

public interface TokenPort {

    String createAccessToken(Long userId, String username, String role);

    String createRefreshToken(Long userId, String username);

    void saveOrUpdateRefreshToken(Long userId, String refreshToken);

    boolean validateToken(String token);

    Long getUserId(String token);

    Optional<String> findRefreshTokenByUserId(Long userId);
}
