package com.ssambbong.gymjjak.diet.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "식단 등록 및 수정 요청")
public record MealAnalysisRequest(
        @Schema(description = "식사 유형", example = "아침", allowableValues = {"아침", "점심", "저녁", "간식"})
        @NotBlank(message = "식사 유형은 필수입니다.")
        String mealType,
        @Schema(description = "식사 일시(초 제외)", example = "2026-07-18 08:30", type = "string")
        @NotNull(message = "식사 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime mealTime,
        @Schema(description = "먹은 메뉴", example = "삶은 계란 2개와 바나나")
        @NotBlank(message = "메뉴는 필수입니다.")
        @Size(max = 255, message = "메뉴는 255자 이하여야 합니다.")
        String menu,
        @Schema(description = "섭취 열량(kcal), 입력하지 않아도 됩니다.", example = "280", nullable = true)
        @PositiveOrZero(message = "kcal은 0 이상이어야 합니다.")
        Long kcal,
        @Schema(description = "탄수화물(g), AI 구독 사용자만 입력 가능", example = "67.00", nullable = true)
        @PositiveOrZero(message = "탄수화물은 0 이상이어야 합니다.")
        @Digits(integer = 6, fraction = 2, message = "탄수화물은 소수점 둘째 자리까지 입력할 수 있습니다.")
        BigDecimal carbohydrate,
        @Schema(description = "단백질(g), AI 구독 사용자만 입력 가능", example = "51.90", nullable = true)
        @PositiveOrZero(message = "단백질은 0 이상이어야 합니다.")
        @Digits(integer = 6, fraction = 2, message = "단백질은 소수점 둘째 자리까지 입력할 수 있습니다.")
        BigDecimal protein,
        @Schema(description = "지방(g), AI 구독 사용자만 입력 가능", example = "7.60", nullable = true)
        @PositiveOrZero(message = "지방은 0 이상이어야 합니다.")
        @Digits(integer = 6, fraction = 2, message = "지방은 소수점 둘째 자리까지 입력할 수 있습니다.")
        BigDecimal fat,
        @Schema(description = "식단 사진의 파일 ID, 입력하지 않아도 됩니다.", example = "15", nullable = true)
        @PositiveOrZero(message = "파일 ID는 0 이상이어야 합니다.")
        Long fileId
) {
}
