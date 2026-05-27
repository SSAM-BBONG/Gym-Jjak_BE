package com.ssambbong.gymjjak.category.presectation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카테고리 등록 요청")
public record CreateCategoryRequest(
        @NotBlank(message = "카테고리명은 필수입니다.")
        @Schema(description = "카테고리명", example = "다이어트")
        String name) {
}
