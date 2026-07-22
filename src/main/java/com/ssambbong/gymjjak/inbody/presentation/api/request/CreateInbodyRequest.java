package com.ssambbong.gymjjak.inbody.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInbodyRequest(
        @Schema(description = "인바디 측정일", example = "2026-07-10")
        @NotNull(message = "측정일은 필수입니다.")
        LocalDate measuredDate,

        @Schema(description = "키(cm)", example = "170.00")
        @NotNull(message = "키는 필수입니다.")
        @DecimalMin(value = "0.01", message = "키는 0보다 커야 합니다.")
        @Digits(integer = 3, fraction = 2, message = "키는 정수 3자리, 소수 2자리까지 입력할 수 있습니다.")
        BigDecimal height,

        @Schema(description = "몸무게(kg)", example = "70.00")
        @NotNull(message = "몸무게는 필수입니다.")
        @DecimalMin(value = "0.01", message = "몸무게는 0보다 커야 합니다.")
        @Digits(integer = 3, fraction = 2, message = "몸무게는 정수 3자리, 소수 2자리까지 입력할 수 있습니다.")
        BigDecimal weight,

        @Schema(description = "체지방률(%)", example = "15.50")
        @DecimalMin(value = "0.00", message = "체지방률은 0 이상이어야 합니다.")
        @Digits(integer = 3, fraction = 2, message = "체지방률은 정수 3자리, 소수 2자리까지 입력할 수 있습니다.")
        BigDecimal bodyFatPercentage,

        @Schema(description = "골격근량(kg)", example = "30.20")
        @DecimalMin(value = "0.00", message = "골격근량은 0 이상이어야 합니다.")
        @Digits(integer = 3, fraction = 2, message = "골격근량은 정수 3자리, 소수 2자리까지 입력할 수 있습니다.")
        BigDecimal skeletalMuscleMass,

        @Schema(description = "기초대사량(kcal)", example = "1600.00")
        @DecimalMin(value = "0.00", message = "기초대사량은 0 이상이어야 합니다.")
        @Digits(
                integer = 4,
                fraction = 2,
                message = "기초대사량은 정수 4자리, 소수 2자리까지 입력할 수 있습니다."
        )
        BigDecimal bmr
) {
}
