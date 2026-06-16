package com.ssambbong.gymjjak.onboarding.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "온보딩 수정 요청")
public record UpdateOnboardingRequest(
        @Schema(description = "운동 목적", example = "체중 감량")
        @NotBlank(message = "운동 목적은 필수입니다.")
        String exerciseGoal,

        @Schema(description = "운동 기간", example = "6개월")
        @NotBlank(message = "운동 기간은 필수입니다.")
        String exercisePeriod,

        @Schema(description = "운동 빈도", example = "주 3회")
        @NotBlank(message = "운동 빈도는 필수입니다.")
        String exerciseFrequency,

        @Schema(description = "선호 운동", example = "헬스")
        @NotBlank(message = "선호 운동은 필수입니다.")
        String preferredExercise,

        @Schema(description = "키", example = "175.5")
        @NotNull(message = "키는 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "키는 0보다 커야 합니다.")
        BigDecimal height,

        @Schema(description = "몸무게", example = "72.3")
        @NotNull(message = "몸무게는 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "몸무게는 0보다 커야 합니다.")
        BigDecimal weight,

        @Schema(description = "선호 지역")
        @Valid
        @NotNull(message = "선호 지역은 필수입니다.")
        RegionRequest region
) {

}
