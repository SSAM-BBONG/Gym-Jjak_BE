package com.ssambbong.gymjjak.organization.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrganizationApplicationCreateRequest(
        // applicantUserId -> CustomUserDetails 추가 시 삭제 예정
        @NotNull(message = "신청자 ID는 필수입니다.")
        @Schema(example = "1")
        Long applicantUserId,

        @NotBlank(message = "요청 로그인 ID는 필수입니다.")
        String requestedLoginId,

        @NotNull(message = "사업자등록증 파일 ID는 필수입니다.")
        @Schema(example = "1")
        Long businessLicenseFileId,

        @NotBlank(message = "사업자등록번호는 필수입니다.")
        String businessRegistrationNumber,

        @NotBlank(message = "상호명은 필수입니다.")
        String businessName,

        @NotBlank(message = "대표자명은 필수입니다.")
        String representativeName,

        @NotBlank(message = "대표자 전화번호는 필수입니다.")
        String representativePhone,

        @NotBlank(message = "개업일자는 필수입니다.")
        @Schema(example = "2024-01-01")
        String openingDate,

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
        String facilityPhone
) {
}
