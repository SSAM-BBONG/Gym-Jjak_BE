package com.ssambbong.gymjjak.category.presectation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카테고리 수정 요청")
public record UpdateCategoryRequest(
        @NotBlank(message = "카테고리명은 필수입니다.")
        @Schema(description = "수정할 카테고리명", example = "벌크업")
        String name
) {
}
