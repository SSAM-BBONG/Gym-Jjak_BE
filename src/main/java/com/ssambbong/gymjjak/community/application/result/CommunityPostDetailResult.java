package com.ssambbong.gymjjak.community.application.result;

import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;

import java.time.LocalDateTime;
import java.util.List;

public record CommunityPostDetailResult(

        Long postId,
        CommunityPostType type,
        String title,
        String content,
        String author,
        LocalDateTime createdAt,
        Long viewCount,
        Long likeCount,
        Long commentCount,
        boolean mine,
        boolean likedByMe,
        List<CommunityCommentResult> comments

) {
}
