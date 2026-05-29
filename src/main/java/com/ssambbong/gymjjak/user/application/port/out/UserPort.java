package com.ssambbong.gymjjak.user.application.port.out;

import com.ssambbong.gymjjak.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserPort {

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    String encode(String rawPassword);


    Optional<User> findByUsername(String username);

    Optional<User> findById(Long userId);

    boolean matchesPassword(String rawPassword, String encodedPassword);

    String createAccessToken(Long userId, String username, String role);

    String createRefreshToken(Long userId, String username);

    void updateLastLoginAt(Long userId, LocalDateTime lastLoginAt);

    void saveOrUpdateRefreshToken(Long userId, String refreshToken);


    boolean validateToken(String token);

    Long getUserId(String token);

    Optional<String> findRefreshTokenByUserId(Long userId);

}
