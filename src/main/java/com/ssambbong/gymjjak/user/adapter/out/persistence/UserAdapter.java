package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.security.jwt.JwtTokenProvider;
import com.ssambbong.gymjjak.user.application.port.out.DeleteWithdrawnUserPort;
import com.ssambbong.gymjjak.user.application.result.FindUserResult;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.domain.model.User;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserPort, DeleteWithdrawnUserPort {

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
        try {
            UserJpaEntity userJpaEntity = userPersistenceMapper.toEntity(user);

            UserJpaEntity savedUserJpaEntity = springDataUserRepository.saveAndFlush(userJpaEntity);

            return userPersistenceMapper.toDomain(savedUserJpaEntity);
        } catch (DataIntegrityViolationException e) {
            throw mapToUserException(e);
        }
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
    public void updateLastLoginAt(Long userId, LocalDateTime lastLoginAt) {
        springDataUserRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.LOGIN_FAILED));

        springDataUserRepository.updateLastLoginAt(userId, lastLoginAt);
    }

    @Override
    public boolean existsByNicknameAndIdNot(String nickname, Long userId) {
        return springDataUserRepository.existsByNicknameAndIdNot(nickname, userId);
    }

    @Override
    public boolean existsByPhoneAndIdNot(String phone, Long userId) {
        return springDataUserRepository.existsByPhoneAndIdNot(phone, userId);
    }

    @Override
    public void withdraw(Long userId, LocalDateTime deletedAt) {
        int updatedCount = springDataUserRepository.withdraw(userId, UserStatus.WITHDRAWN, deletedAt);
        if (updatedCount == 0) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    private RuntimeException mapToUserException(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();

        if (message.contains("uk_users_username")) {
            return new UserException(UserErrorCode.DUPLICATE_USERNAME);
        }

        if (message.contains("uk_users_nickname")) {
            return new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        if (message.contains("uk_users_phone")) {
            return new UserException(UserErrorCode.DUPLICATE_PHONE);
        }

        return e;
    }

    @Override
    public int countWithdrawnUsersBefore(LocalDateTime threshold) {
        return springDataUserRepository.countWithdrawnUsersBefore(threshold);
    }

    @Override
    public int deleteWithdrawnUsersBefore(LocalDateTime threshold, int batchSize) {
        return springDataUserRepository.deleteWithdrawnUsersBefore(threshold, batchSize);
    }

    @Override
    public void updateStatus(Long userId, UserStatus status) {
        springDataUserRepository.updateStatus(userId, status);
    }

    @Override
    public void updatePassword(
            Long userId,
            String encodedPassword,
            LocalDateTime updatedAt
    ) {
        int updatedCount = springDataUserRepository.changePassword(
                userId,
                encodedPassword,
                updatedAt
        );

        if (updatedCount == 0) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    public List<FindUserResult> findUsers(String name, Long cursor, int size) {
        return springDataUserRepository.findUsersByCursor(
                name,
                cursor,
                PageRequest.of(0, size + 1)
        );
    }

    @Override
    public List<FindUserResult> findBlacklistUsers(Long cursor, int size) {
        return springDataUserRepository.findBlacklistUsersByCursor(
                List.of(UserStatus.DAY_7, UserStatus.ETERNAL),
                cursor,
                PageRequest.of(0, size + 1)
        );
    }

}
