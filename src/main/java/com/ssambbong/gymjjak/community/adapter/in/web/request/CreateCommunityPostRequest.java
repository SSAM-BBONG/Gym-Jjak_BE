package com.ssambbong.gymjjak.community.adapter.in.web.request;

import com.ssambbong.gymjjak.community.application.command.CreateCommunityPostCommand;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommunityPostRequest(

        @Schema(description = "유형", example = "FREE")
        @NotNull(message = "게시글 유형은 필수입니다.")
        CommunityPostType type,

        @Schema(description = "제목", example = "여러분 이 헬스장 꼭 가보세요!!")
        @NotBlank(message = "게시글 제목은 필수입니다.")
        @Size(max = 100, message = "게시글 제목은 100자 이하여야 합니다.")
        String title,

        @Schema(description = "내용", example = "이 헬스장 트레이너가 진짜 몸짱에 얼굴도 완전 잘생겼어요!")
        @NotBlank(message = "게시글 내용은 필수입니다.")
        String content

) {

    public CreateCommunityPostCommand toCommand(
            Long userId,
            String role
    ) {
        return new CreateCommunityPostCommand(
                userId,
                role,
                type,
                title,
                content
        );
    }
}
