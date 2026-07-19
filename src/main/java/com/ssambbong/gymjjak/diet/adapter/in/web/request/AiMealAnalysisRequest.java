package com.ssambbong.gymjjak.diet.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "AI 식단 이미지 분석 요청")
public record AiMealAnalysisRequest(
        @NotNull(message = "파일 ID는 필수입니다.")
        @Positive(message = "파일 ID는 1 이상이어야 합니다.")
        @Schema(description = "분석할 음식 이미지 파일 ID", example = "15")
        Long fileId,
        @NotBlank(message = "식사 유형은 필수입니다.")
        @Schema(description = "식사 유형", example = "점심", allowableValues = {"아침", "점심", "저녁", "간식"})
        String mealType,
        @NotNull(message = "식사 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        @Schema(description = "식사 일시", example = "2026-07-18 12:30", type = "string")
        LocalDateTime mealTime
) {
}
