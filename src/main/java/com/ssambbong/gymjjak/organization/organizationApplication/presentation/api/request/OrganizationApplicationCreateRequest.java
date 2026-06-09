package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrganizationApplicationCreateRequest(
        @NotBlank(message = "요청 로그인 ID는 필수입니다.")
        String requestedLoginId,

        @NotBlank(message = "사업자등록번호는 필수입니다.")
        @Pattern(regexp = "^\\d{10}$", message = "사업자등록번호는 10자리 숫자여야 합니다.")
        String businessRegistrationNumber,

        @NotBlank(message = "상호명은 필수입니다.")
        String businessName,

        @NotBlank(message = "대표자명은 필수입니다.")
        String representativeName,

        @NotBlank(message = "대표자 전화번호는 필수입니다.")
        @Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
        String representativePhone,

        @NotNull(message = "개업일자는 필수입니다.")
        @PastOrPresent(message = "개업일자는 현재 또는 과거 날짜여야 합니다.")
        @Schema(example = "2024-01-01")
        LocalDate openingDate,

        @NotBlank(message = "도로명 주소는 필수입니다.")
        String roadAddress,



        // 선택값
        String jibunAddress,
        String detailAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String websiteUrl,
        String instagramUrl,
        String blogUrl,
        @Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 02-1234-5678)")
        String facilityPhone
) {
}
