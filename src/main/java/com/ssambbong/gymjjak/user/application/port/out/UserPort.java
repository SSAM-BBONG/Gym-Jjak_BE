package com.ssambbong.gymjjak.user.application.port.out;

import com.ssambbong.gymjjak.user.application.result.*;
import com.ssambbong.gymjjak.user.domain.model.SocialProvider;
import com.ssambbong.gymjjak.user.domain.model.User;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.List;
import java.util.Optional;

public interface UserPort {

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneAndRole(String phone,  UserRole role);

    String encode(String rawPassword);


    Optional<User> findByUsername(String username);

    Optional<User> findById(Long userId);

    boolean matchesPassword(String rawPassword, String encodedPassword);

    void updateLastLoginAt(Long userId, LocalDateTime lastLoginAt);

    boolean existsByNicknameAndIdNot(String nickname, Long userId);

    boolean existsByPhoneAndIdNot(String phone, Long userId);

    void withdraw(Long userId, LocalDateTime now);

    void updateStatus(Long userId, UserStatus status);

    int activateExpiredSevenDaySuspendedUsers(List<Long> userIds);

    void updatePassword(Long userId, String encodedPassword, LocalDateTime updatedAt);

    PageResult<FindUserResult> findUsers(String keyword, int page, int size);

    PageResult<FindBlacklistUserResult> findBlacklistUsers(String keyword, int page, int size);

    Optional<User> findBySocialProviderAndSocialId(
            SocialProvider socialProvider,
            String socialId
    );

    PageResult<FindTrainerUserResult> findTrainerUsers(String keyword, int page, int size);

    UserUsernameAndNicknameResult findUsernameAndNickname(Long userId);
}
