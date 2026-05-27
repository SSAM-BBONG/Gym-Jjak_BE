package com.ssambbong.gymjjak.report.application.port;

import java.util.Optional;

public interface UserQueryPort {
    Optional<UserProfileView> findUserProfile(Long userId);
}
