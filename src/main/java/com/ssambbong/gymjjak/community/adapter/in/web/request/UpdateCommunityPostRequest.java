package com.ssambbong.gymjjak.community.adapter.in.web.request;

import com.ssambbong.gymjjak.community.application.command.UpdateCommunityPostCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommunityPostRequest(

        @Schema(
                description = "게시글 제목",
                example = "여러분 이 헬스장 진짜 꼭 가보세요!!"
        )
        @NotBlank(message = "게시글 제목은 필수입니다.")
        @Size(
                max = 100,
                message = "게시글 제목은 100자 이하여야 합니다."
        )
        String title,

        @Schema(
                description = "게시글 내용",
                example = "시설도 좋고 트레이너도 친절해서 추천합니다!"
        )
        @NotBlank(message = "게시글 내용은 필수입니다.")
        String content

) {

    public UpdateCommunityPostCommand toCommand(
            Long userId,
            Long postId
    ) {

        return new UpdateCommunityPostCommand(
                userId,
                postId,
                title,
                content
        );
    }
}
