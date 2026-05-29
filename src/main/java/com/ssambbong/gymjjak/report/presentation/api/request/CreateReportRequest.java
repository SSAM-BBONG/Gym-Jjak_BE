package com.ssambbong.gymjjak.report.presentation.api.request;

import com.ssambbong.gymjjak.report.domain.model.ReportReasonType;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "신고 작성 요청")
public record CreateReportRequest(

        @Schema(description = "신고 대상 ID", example = "1")
        @NotNull(message = "신고 대상 ID는 필수입니다.")
        Long targetId,

        @Schema(description = "신고 대상 타입", example = "PT_COURSE")
        @NotNull(message = "신고 대상 타입은 필수입니다.")
        ReportTargetType targetType,

        @Schema(description = "신고 사유 태그", example = "ADVERTISEMENT")
        @NotNull(message = "신고 사유는 필수입니다.")
        ReportReasonType reason,

        @Schema(description = "신고 상세 사유", example = "광고가 너무 심해요")
        @NotBlank(message = "신고 상세 사유는 필수입니다.")
        @Size(max = 1000, message = "신고 상세 사유는 1000자 이하여야 합니다.")
        String detail
) {
}
