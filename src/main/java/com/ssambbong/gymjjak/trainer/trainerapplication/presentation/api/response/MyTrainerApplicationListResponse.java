package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.MyTrainerApplicationListResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record MyTrainerApplicationListResponse(

        @Schema(description = "트레이너 신청 목록")
        List<MyTrainerApplicationSummaryResponse> content,

        @Schema(description = "현재 페이지", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int size,

        @Schema(description = "전체 신청 건수", example = "3")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "1")
        int totalPages,

        @Schema(description = "다음 페이지 존재 여부", example = "false")
        boolean hasNext
) {
    public static MyTrainerApplicationListResponse from(MyTrainerApplicationListResult result) {
        return MyTrainerApplicationListResponse.builder()
                .content(
                        result.content().stream()
                                .map(MyTrainerApplicationSummaryResponse::from)
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
