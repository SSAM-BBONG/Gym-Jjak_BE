package com.ssambbong.gymjjak.user.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "커서 기반 목록 응답")
public record CursorResponse<T>(
        @Schema(description = "응답 데이터")
        List<T> content,

        @Schema(description = "다음 커서")
        Long nextCursor,

        @Schema(description = "다음 데이터 존재 여부")
        boolean hasNext
) {
}
