package com.ssambbong.gymjjak.diet.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "AI 식단 이미지 분석 요청")
public record AiMealAnalysisRequest(
        @NotNull(message = "식단 이미지 정보는 필수입니다.")
        @Valid
        @Schema(description = "S3 업로드를 완료한 식단 이미지 메타데이터")
        UploadedFileMetadataRequest file,
        @NotBlank(message = "식사 유형은 필수입니다.")
        @Schema(description = "식사 유형", example = "점심", allowableValues = {"아침", "점심", "저녁", "간식"})
        String mealType,
        @NotNull(message = "식사 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        @Schema(description = "식사 일시", example = "2026-07-18 12:30", type = "string")
        LocalDateTime mealTime
) {
}
