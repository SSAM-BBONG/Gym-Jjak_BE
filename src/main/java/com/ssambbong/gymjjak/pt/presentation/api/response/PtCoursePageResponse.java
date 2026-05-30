package com.ssambbong.gymjjak.pt.presentation.api.response;

import com.ssambbong.gymjjak.pt.application.usecase.PtCourseQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// PT 강습 목록 페이지네이션 응답
public record PtCoursePageResponse(

        @Schema(description = "PT 강습 목록")
        List<PtCourseViewResponse> content,

        @Schema(description = "전체 데이터 수", example = "5")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "1")
        int totalPages,

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int size

) {
    public static PtCoursePageResponse from(PtCourseQueryUseCase.PtCoursePageResult result) {
        List<PtCourseViewResponse> content = result.content()
                .stream()
                .map(PtCourseViewResponse::from)
                .toList();
        return new PtCoursePageResponse(
                content,
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }
}
