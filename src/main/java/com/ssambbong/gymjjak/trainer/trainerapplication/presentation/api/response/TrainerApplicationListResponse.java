package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationSummaryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record TrainerApplicationListResponse(

        @Schema(description = "트레이너 신청 목록")
        List<TrainerApplicationSummaryResponse> content,

        @Schema(description = "현재 페이지 정보", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "20")
        int size,

        @Schema(description = "전체 데이터 개수", example = "42")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "1")
        int totalPages,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {

    public static TrainerApplicationListResponse from(TrainerApplicationListResult result) {
        return TrainerApplicationListResponse.builder()
                .content(
                        result.content().stream()
                                .map(TrainerApplicationSummaryResponse::from)
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
