package com.ssambbong.gymjjak.user.application.result;

public record UserUsernameAndNicknameResult(
        String username,
        String nickname,
        boolean socialUser,
        long communityPostCount
) {

    public UserUsernameAndNicknameResult(
            String username,
            String nickname,
            boolean socialUser
    ) {
        this(username, nickname, socialUser, 0L);
    }

    public UserUsernameAndNicknameResult withCommunityPostCount(long communityPostCount) {
        return new UserUsernameAndNicknameResult(
                username,
                nickname,
                socialUser,
                communityPostCount
        );
    }
}
