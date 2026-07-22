package com.ssambbong.gymjjak.community.application.command;

public record DeleteCommunityPostCommand(
        Long userId,
        Long postId
) {
}
