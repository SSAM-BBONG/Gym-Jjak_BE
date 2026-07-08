package com.ssambbong.gymjjak.community.application.command;

public record UpdateCommunityPostCommand(

        Long userId,
        Long postId,
        String title,
        String content

) {
}
