package com.ssambbong.gymjjak.community.application.command;

public record UpdateCommunityCommentCommand(

        Long userId,
        Long commentId,
        String content

) {
}
