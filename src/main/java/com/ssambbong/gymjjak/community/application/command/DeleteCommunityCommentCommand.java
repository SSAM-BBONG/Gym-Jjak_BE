package com.ssambbong.gymjjak.community.application.command;

public record DeleteCommunityCommentCommand(

        Long userId,
        Long commentId

) {
}
