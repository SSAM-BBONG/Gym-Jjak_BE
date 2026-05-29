package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.global.security.jwt.JwtTokenProvider;
import com.ssambbong.gymjjak.user.application.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.application.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserPersistenceMapper userPersistenceMapper;
    private final PasswordEncoder passwordEncoder;
    private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public User save(User user) {


        UserJpaEntity userJpaEntity = userPersistenceMapper.toEntity(user);

        UserJpaEntity savedUserJpaEntity = springDataUserRepository.save(userJpaEntity);

        return userPersistenceMapper.toDomain(savedUserJpaEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return springDataUserRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return springDataUserRepository.existsByPhone(phone);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataUserRepository.findByUsername(username)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return springDataUserRepository.findById(userId)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String createAccessToken(Long userId, String username, String role) {
        return jwtTokenProvider.createAccessToken(userId, username, role);
    }

    @Override
    public String createRefreshToken(Long userId, String username) {
        return jwtTokenProvider.createRefreshToken(userId, username);
    }

    @Override
    public void updateLastLoginAt(Long userId, LocalDateTime lastLoginAt) {
        UserJpaEntity userJpaEntity = springDataUserRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.LOGIN_FAILED));

        userJpaEntity.updateLastLoginAt(lastLoginAt);
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
