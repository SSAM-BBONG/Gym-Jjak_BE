package com.ssambbong.gymjjak.community.application.result;

import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;

import java.time.LocalDateTime;

public record CommunityPostListResult(
        Long postId,
        CommunityPostType type,
        String title,
        String content,
        String author,
        LocalDateTime createdAt,
        Long viewCount,
        Long likeCount,
        Long commentCount
) {
}
