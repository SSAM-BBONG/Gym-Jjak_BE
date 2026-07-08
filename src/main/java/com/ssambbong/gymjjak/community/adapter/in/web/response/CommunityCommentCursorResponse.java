package com.ssambbong.gymjjak.community.adapter.in.web.response;

import com.ssambbong.gymjjak.community.application.result.CommunityCommentCursorResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CommunityCommentCursorResponse(

        @Schema(description = "댓글 목록")
        List<CommunityCommentResponse> content,

        @Schema(
                description = "다음 댓글 조회 Cursor ID",
                example = "20"
        )
        Long nextCursorId,

        @Schema(
                description = "다음 댓글 존재 여부",
                example = "true"
        )
        boolean hasNext

) {

    public static CommunityCommentCursorResponse from(
            CommunityCommentCursorResult result
    ) {

        return new CommunityCommentCursorResponse(
                result.content()
                        .stream()
                        .map(CommunityCommentResponse::from)
                        .toList(),
                result.nextCursorId(),
                result.hasNext()
        );
    }
}
