package com.ssambbong.gymjjak.pt.presentation.api.response;

import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record PtCourseViewResponse(
        @Schema(description = "PT 강습 ID", example = "1")
        Long ptCourseId,

        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,

        @Schema(description = "태그 ID", example = "1")
        Long tagId,

        @Schema(description = "썸네일 URL", example = "https://...")
        String thumbnailUrl,

        @Schema(description = "PT 강습 제목", example = "체계적인 가슴 집중 PT")
        String title,

        @Schema(description = "PT 강습 소개", example = "가슴 근육 발달에 특화된 12주 프로그램")
        String description,

        @Schema(description = "가격", example = "50000")
        int price,

        @Schema(description = "전체 회차 수", example = "12")
        int totalSessionCount,

        @Schema(description = "PT 강습 상태", example = "VISIBLE")
        PtCourseStatus status
) {
    public static PtCourseViewResponse from(PtCourseQueryUseCase.PtCourseView view) {
        return new PtCourseViewResponse(
                view.ptCourseId(),
                view.categoryId(),
                view.tagId(),
                view.thumbnailUrl(),
                view.title(),
                view.description(),
                view.price(),
                view.totalSessionCount(),
                view.status()
        );
    }
}
