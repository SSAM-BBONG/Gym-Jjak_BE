package com.ssambbong.gymjjak.diet.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "일일 영양 목표 등록 요청")
public record NutritionGoalRequest(
        @NotNull(message = "목표 단백질은 필수입니다.")
        @PositiveOrZero(message = "목표 단백질은 0 이상이어야 합니다.")
        @Schema(description = "일일 목표 단백질(g). 0 이상이며 상한은 없습니다.", example = "120", minimum = "0")
        Long goalProtein,
        @NotNull(message = "목표 탄수화물은 필수입니다.")
        @PositiveOrZero(message = "목표 탄수화물은 0 이상이어야 합니다.")
        @Schema(description = "일일 목표 탄수화물(g). 0 이상이며 상한은 없습니다.", example = "250", minimum = "0")
        Long goalCarbohydrate,
        @NotNull(message = "목표 지방은 필수입니다.")
        @PositiveOrZero(message = "목표 지방은 0 이상이어야 합니다.")
        @Schema(description = "일일 목표 지방(g). 0 이상이며 상한은 없습니다.", example = "60", minimum = "0")
        Long goalFat,
        @NotNull(message = "목표 칼로리는 필수입니다.")
        @PositiveOrZero(message = "목표 칼로리는 0 이상이어야 합니다.")
        @Schema(description = "일일 목표 칼로리(kcal). 0 이상이며 상한은 없습니다.", example = "2000", minimum = "0")
        Long dailyGoalKcal) {}
