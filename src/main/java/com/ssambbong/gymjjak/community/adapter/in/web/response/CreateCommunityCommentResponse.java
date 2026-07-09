package com.ssambbong.gymjjak.community.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateCommunityCommentResponse(

        @Schema(
                description = "생성된 댓글 ID",
                example = "1"
        )
        Long commentId

) {

    public static CreateCommunityCommentResponse from(
            Long commentId
    ) {

        return new CreateCommunityCommentResponse(
                commentId
        );
    }
}
