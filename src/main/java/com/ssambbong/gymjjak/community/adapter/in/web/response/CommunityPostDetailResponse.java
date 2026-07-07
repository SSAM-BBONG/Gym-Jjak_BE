package com.ssambbong.gymjjak.community.adapter.in.web.response;

import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record CommunityPostDetailResponse(

        @Schema(description = "게시글 ID", example = "1")
        Long postId,

        @Schema(description = "게시글 유형", example = "FREE")
        CommunityPostType type,

        @Schema(
                description = "게시글 제목",
                example = "여러분 이 헬스장 꼭 가보세요!!"
        )
        String title,

        @Schema(
                description = "게시글 내용",
                example = "이 헬스장 트레이너가 진짜 몸짱이에요!"
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

        @Schema(description = "조회수", example = "10")
        Long viewCount,

        @Schema(description = "좋아요 수", example = "5")
        Long likeCount,

        @Schema(description = "댓글 수", example = "2")
        Long commentCount,

        @Schema(
                description = "현재 로그인 사용자의 게시글 여부",
                example = "true"
        )
        boolean mine,

        @Schema(
                description = "현재 로그인 사용자의 좋아요 여부",
                example = "false"
        )
        boolean likedByMe,

        @Schema(description = "댓글 목록")
        List<CommunityCommentResponse> comments

) {

    public static CommunityPostDetailResponse from(
            CommunityPostDetailResult result
    ) {

        return new CommunityPostDetailResponse(
                result.postId(),
                result.type(),
                result.title(),
                result.content(),
                result.author(),
                result.createdAt(),
                result.viewCount(),
                result.likeCount(),
                result.commentCount(),
                result.mine(),
                result.likedByMe(),
                result.comments()
                        .stream()
                        .map(CommunityCommentResponse::from)
                        .toList()
        );
    }
}
