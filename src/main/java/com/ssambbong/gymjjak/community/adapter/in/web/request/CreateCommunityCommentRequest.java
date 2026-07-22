package com.ssambbong.gymjjak.community.adapter.in.web.request;

import com.ssambbong.gymjjak.community.application.command.CreateCommunityCommentCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateCommunityCommentRequest(

        @Schema(description = "댓글 내용", example = "저도 이 헬스장 가보고 싶어요!")
        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content

) {

    public CreateCommunityCommentCommand toCommand(
            Long userId,
            Long postId
    ) {

        return new CreateCommunityCommentCommand(
                userId,
                postId,
                content
        );
    }
}
