package com.ssambbong.gymjjak.community.application.command;

public record DeleteCommunityPostLikeCommand(

        Long userId,
        Long postId

) {
}
