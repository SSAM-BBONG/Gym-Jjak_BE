package com.ssambbong.gymjjak.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "organization_applications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationApplicationJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_application_id")
    private Long organizationApplicationId;

    @Column(name = "applicant_user_id", nullable = false)
    private Long applicantUserId;

    @Column(name = "requested_login_id", nullable = false, unique = true, length = 100)
    private String requestedLoginId;

    @Column(name = "business_license_file_id", nullable = false)
    private Long businessLicenseFileId;

    @Column(name = "business_registration_number", nullable = false, length = 30)
    private String businessRegistrationNumber;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "representative_name", nullable = false, length = 50)
    private String representativeName;

    @Column(name = "representative_phone", nullable = false, length = 20)
    private String representativePhone;

    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @Column(name = "road_address", nullable = false, length = 255)
    private String roadAddress;

    @Column(name = "jibun_address", length = 255)
    private String jibunAddress;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;

    @Column(name = "blog_url", length = 255)
    private String blogUrl;

    @Column(name = "facility_phone", length = 20)
    private String facilityPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrganizationApplicationStatus status;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // 도메인 → JpaEntity 변환
    public static OrganizationApplicationJpaEntity fromDomain(OrganizationApplication domain) {
        OrganizationApplicationJpaEntity entity = new OrganizationApplicationJpaEntity();
        entity.applicantUserId = domain.getApplicantUserId();
        entity.requestedLoginId = domain.getRequestedLoginId();
        entity.businessLicenseFileId = domain.getBusinessLicenseFileId();
        entity.businessRegistrationNumber = domain.getBusinessRegistrationNumber();
        entity.businessName = domain.getBusinessName();
        entity.representativeName = domain.getRepresentativeName();
        entity.representativePhone = domain.getRepresentativePhone();
        entity.openingDate = domain.getOpeningDate();
        entity.roadAddress = domain.getRoadAddress();
        entity.jibunAddress = domain.getJibunAddress();
        entity.detailAddress = domain.getDetailAddress();
        entity.latitude = domain.getLatitude();
        entity.longitude = domain.getLongitude();
        entity.websiteUrl = domain.getWebsiteUrl();
        entity.instagramUrl = domain.getInstagramUrl();
        entity.blogUrl = domain.getBlogUrl();
        entity.facilityPhone = domain.getFacilityPhone();
        entity.status = domain.getStatus();
        entity.rejectReason = domain.getRejectReason();
        return entity;
    }

    public OrganizationApplication toDomain() {
        return OrganizationApplication.restore(
                this.organizationApplicationId,
                this.applicantUserId,
                this.requestedLoginId,
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
                this.websiteUrl,
                this.instagramUrl,
                this.blogUrl,
                this.facilityPhone,
                this.status,
                this.rejectReason,
                this.getCreatedAt(),
                this.getUpdatedAt()
        );
    }

}
