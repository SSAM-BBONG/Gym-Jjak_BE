package com.ssambbong.gymjjak.user.application.result;

import com.ssambbong.gymjjak.user.domain.model.BlacklistType;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;

public record FindBlacklistUserResult(
        Long userId,
        String username,
        String name,
        String nickname,
        UserStatus status,
        BlacklistType blacklistType,
        String reason
) {
}
