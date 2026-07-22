package com.ssambbong.gymjjak.report.application.port.user;

import java.util.Optional;

public interface UserQueryPort {
    Optional<UserProfileView> findUserProfile(Long userId);
}
