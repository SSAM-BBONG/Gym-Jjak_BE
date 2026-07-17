package com.ssambbong.gymjjak.diet.adapter.in.web.response;

import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "식단 목록 페이지 응답")
public record MealAnalysisPageResponse(
        @Schema(description = "조회된 식단 목록")
        List<MealAnalysisResponse> meals,
        @Schema(description = "현재 페이지 번호(0부터 시작)", example = "0")
        int page,
        @Schema(description = "페이지당 조회 개수", example = "20")
        int size,
        @Schema(description = "전체 식단 개수", example = "35")
        long totalElements,
        @Schema(description = "전체 페이지 수", example = "2")
        int totalPages,
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
    public static MealAnalysisPageResponse from(Page<MealAnalysisResponse> page) {
        return new MealAnalysisPageResponse(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.hasNext());
    }
}
