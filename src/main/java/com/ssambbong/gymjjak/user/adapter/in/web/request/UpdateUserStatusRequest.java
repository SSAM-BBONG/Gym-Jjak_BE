package com.ssambbong.gymjjak.user.adapter.in.web.request;

import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 상태 변경")
public record UpdateUserStatusRequest(
        @Schema(description = "상태", example = "DAY_7")
        UserStatus status,

        @Schema(description = "제재 사유", example = "부적절한 게시글 반복 작성")
        String reason

) {
}
