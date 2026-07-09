package com.ssambbong.gymjjak.community.application.command;

public record CreateCommunityCommentCommand(

        Long userId,
        Long postId,
        String content

) {
}
