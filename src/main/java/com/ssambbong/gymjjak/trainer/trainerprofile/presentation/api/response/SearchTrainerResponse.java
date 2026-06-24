package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SearchTrainerResponse(

        @Schema(description = "트레이너 프로필 ID", example = "7")
        Long trainerProfileId,

        @Schema(description = "트레이너 이름", example = "홍길동")
        String name,

        @Schema(
                description = "트레이너 로그인 아이디",
                example = "trainer01@test.com"
        )
        String username,

        @Schema(description = "트레이너 닉네임", example = "운동왕")
        String nickname
) {

    public static SearchTrainerResponse from(SearchTrainerResult result) {
        return SearchTrainerResponse.builder()
                .trainerProfileId(result.trainerProfileId())
                .name(result.name())
                .username(result.username())
                .nickname(result.nickname())
                .build();
    }
}
