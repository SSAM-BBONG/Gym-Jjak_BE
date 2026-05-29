package com.ssambbong.gymjjak.organization.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class OrganizationApplication {

    private final Long organizationApplicationId;
    private final Long applicantUserId;
    private final String requestedLoginId;
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
    private final OrganizationApplicationStatus status;
    private final String rejectReason;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private OrganizationApplication(
            Long organizationApplicationId,
            Long applicantUserId,
            String requestedLoginId,
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
            OrganizationApplicationStatus status,
            String rejectReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.organizationApplicationId = organizationApplicationId;
        this.applicantUserId = applicantUserId;
        this.requestedLoginId = requestedLoginId;
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
        this.rejectReason = rejectReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 신규 신청 생성 (id=null, status=PENDING)
    public static OrganizationApplication create(
            Long applicantUserId,
            String requestedLoginId,
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
            String facilityPhone
    ) {
        return new OrganizationApplication(
                null,
                applicantUserId,
                requestedLoginId,
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
                OrganizationApplicationStatus.PENDING,
                null,
                null,
                null
        );
    }

    public static OrganizationApplication restore(
            Long organizationApplicationId,
            Long applicantUserId,
            String requestedLoginId,
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
            OrganizationApplicationStatus status,
            String rejectReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new OrganizationApplication(
                organizationApplicationId,
                applicantUserId,
                requestedLoginId,
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
                status,
                rejectReason,
                createdAt,
                updatedAt
        );
    }

}
