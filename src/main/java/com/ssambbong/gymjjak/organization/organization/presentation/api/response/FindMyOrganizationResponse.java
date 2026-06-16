package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FindMyOrganizationResponse(

        // 기본 정보 (수정 불가)
        String requestedLoginId,
        String businessRegistrationNumber,
        String businessName,
        String representativeName,
        String representativePhone,
        LocalDate openingDate,
        String roadAddress,
        String jibunAddress,
        String detailAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        Long businessLicenseFileId,

        // 추가 정보 (수정 가능)
        String facilityPhone,
        String instagramUrl,
        String blogUrl,
        String websiteUrl
) {
    public static FindMyOrganizationResponse of(Organization organization) {
        return new FindMyOrganizationResponse(
                null, // requestedLoginId는 users 테이블에 있어서 추후 UserQueryPort로 조회 예정
                organization.getBusinessRegistrationNumber(),
                organization.getBusinessName(),
                organization.getRepresentativeName(),
                organization.getRepresentativePhone(),
                organization.getOpeningDate(),
                organization.getRoadAddress(),
                organization.getJibunAddress(),
                organization.getDetailAddress(),
                organization.getLatitude(),
                organization.getLongitude(),
                organization.getBusinessLicenseFileId(),
                organization.getFacilityPhone(),
                organization.getInstagramUrl(),
                organization.getBlogUrl(),
                organization.getWebsiteUrl()
        );
    }
}
