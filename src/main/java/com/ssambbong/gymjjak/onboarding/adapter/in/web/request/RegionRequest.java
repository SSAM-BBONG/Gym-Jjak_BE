package com.ssambbong.gymjjak.onboarding.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "선호 지역 요청")
public record RegionRequest(

        @Schema(description = "시/도", example = "서울")
        @NotBlank(message = "시/도는 필수입니다.")
        String sido,

        @Schema(description = "시/군/구", example = "강남구")
        @NotBlank(message = "시/군/구는 필수입니다.")
        String sigungu,

        @Schema(description = "읍/면/동", example = "역삼동")
        @NotBlank(message = "읍/면/동은 필수입니다.")
        String eupmyeondong,

        @Schema(description = "전체 주소", example = "서울 강남구 역삼동 테헤란로 123")
        @NotBlank(message = "전체 주소는 필수입니다.")
        String fullName,

        @Schema(description = "위도", example = "37.5665")
        @NotNull(message = "위도는 필수입니다.")
        BigDecimal latitude,

        @Schema(description = "경도", example = "127.9780")
        @NotNull(message = "경도는 필수입니다.")
        BigDecimal longitude
) {
}
