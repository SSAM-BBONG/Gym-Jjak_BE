package com.ssambbong.gymjjak.community.adapter.in.web.response;

import com.ssambbong.gymjjak.community.application.result.CommunityCommentResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CommunityCommentResponse(@Schema(
        description = "댓글 ID",
        example = "1"
)
                                       Long commentId,

                                       @Schema(
                                               description = "댓글 작성자 닉네임",
                                               example = "왕구리"
                                       )
                                       String author,

                                       @Schema(
                                               description = "댓글 작성 일시",
                                               example = "2026-07-07T21:30:00"
                                       )
                                       LocalDateTime createdAt,

                                       @Schema(
                                               description = "댓글 내용",
                                               example = "저도 가보고 싶어요!"
                                       )
                                       String content,

                                       @Schema(
                                               description = "현재 로그인 사용자의 댓글 여부",
                                               example = "true"
                                       )
                                       boolean mine

) {

    public static CommunityCommentResponse from(
            CommunityCommentResult result
    ) {

        return new CommunityCommentResponse(
                result.commentId(),
                result.author(),
                result.createdAt(),
                result.content(),
                result.mine()
        );
    }
}
