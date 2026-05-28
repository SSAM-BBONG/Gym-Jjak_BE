package com.ssambbong.gymjjak.user.application.port.out;

import com.ssambbong.gymjjak.user.domain.model.User;

import java.util.Optional;

public interface UserPort {

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    String encode(String rawPassword);


    Optional<User> findByUsername(String username);

    boolean matchesPassword(String rawPassword, String encodedPassword);

    String createAccessToken(Long userId, String username, String role);

    String createRefreshToken(Long userId, String username);

    void saveOrUpdateRefreshToken(Long userId, String refreshToken);
}
