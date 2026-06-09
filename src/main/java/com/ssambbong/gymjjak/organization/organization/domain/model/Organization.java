package com.ssambbong.gymjjak.organization.organization.domain.model;

import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplicationStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class Organization {

    private final Long organizationId;
    private final Long organizationAccountId;
    private final Long ownerUserId;
    private final Long applicationId;
    private final Long businessLicenseFileId;
    private final String businessRegistrationNumber;
    private final String businessName;
    private final String representativeName;
    private final String representativePhone;
    private final LocalDate openingDate;
    private final String roadAddress;
    private final String jibunAddress;
    private final String detailAddress;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String websiteUrl;
    private final String instagramUrl;
    private final String blogUrl;
    private final String facilityPhone;
    private final OrganizationStatus status;

    private Organization(
            Long organizationId,
            Long organizationAccountId,
            Long ownerUserId,
            Long applicationId,
            Long businessLicenseFileId,
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
            String websiteUrl,
            String instagramUrl,
            String blogUrl,
            String facilityPhone,
            OrganizationStatus status
    ) {
        this.organizationId = organizationId;
        this.organizationAccountId = organizationAccountId;
        this.ownerUserId = ownerUserId;
        this.applicationId = applicationId;
        this.businessLicenseFileId = businessLicenseFileId;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.businessName = businessName;
        this.representativeName = representativeName;
        this.representativePhone = representativePhone;
        this.openingDate = openingDate;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.detailAddress = detailAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.websiteUrl = websiteUrl;
        this.instagramUrl = instagramUrl;
        this.blogUrl = blogUrl;
        this.facilityPhone = facilityPhone;
        this.status = status;
    }

    // 승인된 신청서로부터 조직 생성
    public static Organization create(
            Long organizationAccountId,
            OrganizationApplication application
    ) {
        if (application.getStatus() != OrganizationApplicationStatus.ACCEPTED) {
            throw new IllegalStateException("승인된 신청서만 조직을 생성할 수 있습니다.");
        }

        return new Organization(
                null,
                organizationAccountId,
                application.getApplicantUserId(),
                application.getOrganizationApplicationId(),
                application.getBusinessLicenseFileId(),
                application.getBusinessRegistrationNumber(),
                application.getBusinessName(),
                application.getRepresentativeName(),
                application.getRepresentativePhone(),
                application.getOpeningDate(),
                application.getRoadAddress(),
                application.getJibunAddress(),
                application.getDetailAddress(),
                application.getLatitude(),
                application.getLongitude(),
                application.getWebsiteUrl(),
                application.getInstagramUrl(),
                application.getBlogUrl(),
                application.getFacilityPhone(),
                OrganizationStatus.ACTIVE
        );
    }

    // 수정 가능한 추가 정보만 변경
    public Organization update(
            String facilityPhone,
            String instagramUrl,
            String blogUrl,
            String websiteUrl
    ) {
        return new Organization(
                this.organizationId,
                this.organizationAccountId,
                this.ownerUserId,
                this.applicationId,
                this.businessLicenseFileId,
                this.businessRegistrationNumber,
                this.businessName,
                this.representativeName,
                this.representativePhone,
                this.openingDate,
                this.roadAddress,
                this.jibunAddress,
                this.detailAddress,
                this.latitude,
                this.longitude,
                websiteUrl,
                instagramUrl,
                blogUrl,
                facilityPhone,
                this.status
        );
    }

    public static Organization restore(
            Long organizationId,
            Long organizationAccountId,
            Long ownerUserId,
            Long applicationId,
            Long businessLicenseFileId,
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
            String websiteUrl,
            String instagramUrl,
            String blogUrl,
            String facilityPhone,
            OrganizationStatus status
    ) {
        return new Organization(
                organizationId,
                organizationAccountId,
                ownerUserId,
                applicationId,
                businessLicenseFileId,
                businessRegistrationNumber,
                businessName,
                representativeName,
                representativePhone,
                openingDate,
                roadAddress,
                jibunAddress,
                detailAddress,
                latitude,
                longitude,
                websiteUrl,
                instagramUrl,
                blogUrl,
                facilityPhone,
                status
        );
    }
}
