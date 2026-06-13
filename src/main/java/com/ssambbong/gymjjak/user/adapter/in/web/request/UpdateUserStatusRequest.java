package com.ssambbong.gymjjak.user.adapter.in.web.request;

import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 상태 변경")
public record UpdateUserStatusRequest(
        @Schema(description = "상태", example = "ACTIVE")
        UserStatus status
) {
}
