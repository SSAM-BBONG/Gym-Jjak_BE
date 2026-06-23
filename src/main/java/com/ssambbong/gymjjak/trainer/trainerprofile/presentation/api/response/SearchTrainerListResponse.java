package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerListResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record SearchTrainerListResponse(

        @Schema(description = "검색된 트레이너 목록")
        List<SearchTrainerResponse> content,

        @Schema(description = "현재 페이지 번호", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int size,

        @Schema(description = "전체 트레이너 수", example = "25")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "3")
        int totalPages,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {

    public static SearchTrainerListResponse from(SearchTrainerListResult result) {
        return SearchTrainerListResponse.builder()
                .content(
                        result.content().stream()
                                .map(SearchTrainerResponse::from)
                                .toList()
                )
                .page(result.page())
                .size(result.size())
                .totalElements(result.totalElements())
                .totalPages(result.totalPages())
                .hasNext(result.hasNext())
                .build();
    }
}
