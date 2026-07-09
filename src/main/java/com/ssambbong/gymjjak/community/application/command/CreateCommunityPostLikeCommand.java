package com.ssambbong.gymjjak.community.application.command;

public record CreateCommunityPostLikeCommand(

        Long userId,
        Long postId

) {
}
