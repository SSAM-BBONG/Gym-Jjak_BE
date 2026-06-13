package com.ssambbong.gymjjak.user.application.result;

import com.ssambbong.gymjjak.user.domain.model.User;

public record UserProfileResult(
        String name,
        String nickname,
        String phone
) {
    public static UserProfileResult from(User user) {
        return new UserProfileResult(
                user.getName(),
                user.getNickname(),
                user.getPhone()
        );
    }
}
