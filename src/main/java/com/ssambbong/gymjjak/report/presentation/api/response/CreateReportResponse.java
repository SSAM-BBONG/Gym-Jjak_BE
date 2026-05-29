package com.ssambbong.gymjjak.report.presentation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "신고 등록 응답")
public class CreateReportResponse {
    @Schema(description = "생성된 신고 Id", example = "1")
    Long reportId;
}
