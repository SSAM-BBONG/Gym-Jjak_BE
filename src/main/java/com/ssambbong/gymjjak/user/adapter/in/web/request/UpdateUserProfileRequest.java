package com.ssambbong.gymjjak.user.adapter.in.web.request;

import com.ssambbong.gymjjak.user.application.command.UpdateProfileCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "프로필 정보 수정 요청")
public record UpdateUserProfileRequest(

        @Schema(description = "이름", example = "서주원2")
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @Schema(description = "닉네임", example = "섹시킹2")
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @Schema(description = "전화번호", example = "010-1111-3333")
        @NotBlank(message = "전화번호는 필수입니다.")
        String phone

) {
        public UpdateProfileCommand toCommand(Long userId) {
                return new UpdateProfileCommand(
                        userId,
                        name,
                        nickname,
                        phone
                );
        }
}
