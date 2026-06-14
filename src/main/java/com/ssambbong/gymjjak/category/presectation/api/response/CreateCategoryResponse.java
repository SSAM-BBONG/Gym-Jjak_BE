package com.ssambbong.gymjjak.category.presectation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 등록 응답")
public record CreateCategoryResponse(
        @Schema(description = "생성된 카테고리 ID", example = "5")
        Long categoryId
) {
}
