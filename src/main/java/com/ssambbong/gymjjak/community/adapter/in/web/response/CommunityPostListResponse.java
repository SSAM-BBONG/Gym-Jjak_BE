package com.ssambbong.gymjjak.community.adapter.in.web.response;

import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CommunityPostListResponse(

        @Schema(description = "게시글 ID", example = "1")
        Long postId,

        @Schema(
                description = "게시글 유형",
                example = "FREE"
        )
        CommunityPostType type,

        @Schema(
                description = "게시글 제목",
                example = "헬스장 추천해주세요"
        )
        String title,

        @Schema(
                description = "게시글 내용",
                example = "강남 쪽 헬스장 추천 부탁드립니다."
        )
        String content,

        @Schema(
                description = "작성자 닉네임",
                example = "왕구리"
        )
        String author,

        @Schema(
                description = "게시글 작성 일시",
                example = "2026-07-07T21:20:00"
        )
        LocalDateTime createdAt,

        @Schema(description = "조회수", example = "120")
        Long viewCount,

        @Schema(description = "좋아요 수", example = "15")
        Long likeCount,

        @Schema(description = "댓글 수", example = "4")
        Long commentCount

) {

    public static CommunityPostListResponse from(
            CommunityPostListResult result
    ) {

        return new CommunityPostListResponse(
                result.postId(),
                result.type(),
                result.title(),
                result.content(),
                result.author(),
                result.createdAt(),
                result.viewCount(),
                result.likeCount(),
                result.commentCount()
        );
    }
}
