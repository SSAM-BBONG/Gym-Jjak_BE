package com.ssambbong.gymjjak.community.adapter.in.web.request;

import com.ssambbong.gymjjak.community.application.command.UpdateCommunityCommentCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCommunityCommentRequest(

        @Schema(
                description = "수정할 댓글 내용",
                example = "저도 직접 가봤는데 정말 좋았어요!"
        )
        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content

) {

    public UpdateCommunityCommentCommand toCommand(
            Long userId,
            Long commentId
    ) {

        return new UpdateCommunityCommentCommand(
                userId,
                commentId,
                content
        );
    }
}
