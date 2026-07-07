package com.ssambbong.gymjjak.community.application.result;

import java.time.LocalDateTime;

public record CommunityCommentResult(

        Long commentId,
        String author,
        LocalDateTime createdAt,
        String content,
        boolean mine
) {
}
