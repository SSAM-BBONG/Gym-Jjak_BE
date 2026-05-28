package com.ssambbong.gymjjak.pt.presectation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "PT 강습 등록 요청")
public record CreatePtCourseRequest(

        @Schema(description = "PT 강습 제목", example = "체계적인 가슴 집중 PT")
        @NotBlank
        String title,

        @Schema(description = "PT 강습 소개", example = "가슴 근육 발달에 특화된 12주 프로그램입니다.")
        @NotBlank
        String description,

        @Schema(description = "카테고리 ID", example = "1")
        @NotNull
        Long categoryId,

        @Schema(description = "태그 ID", example = "1")
        @NotNull
        Long tagId,

        @Schema(description = "썸네일 파일 ID (선택)", example = "10")
        Long thumbnailFileId,

        @Schema(description = "가격 (1회당)", example = "50000")
        @Min(0)
        int price,

        @Schema(description = "전체 회차 수", example = "12")
        @Min(1)
        int totalSessionCount
) {
}