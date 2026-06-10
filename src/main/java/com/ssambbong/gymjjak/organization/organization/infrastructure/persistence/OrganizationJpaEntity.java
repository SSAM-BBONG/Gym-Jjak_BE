package com.ssambbong.gymjjak.organization.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "organizations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "organization_account_id", nullable = false, unique = true)
    private Long organizationAccountId;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "application_id", nullable = false, unique = true)
    private Long applicationId;

    @Column(name = "business_license_file_id", nullable = false)
    private Long businessLicenseFileId;

    @Column(name = "business_registration_number", nullable = false, length = 30, unique = true)
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
    private OrganizationStatus status;

    public void update(Organization organization) {
        this.facilityPhone = organization.getFacilityPhone();
        this.instagramUrl = organization.getInstagramUrl();
        this.blogUrl = organization.getBlogUrl();
        this.websiteUrl = organization.getWebsiteUrl();
    }

    public static OrganizationJpaEntity fromDomain(Organization organization) {
        OrganizationJpaEntity entity = new OrganizationJpaEntity();
        entity.organizationAccountId = organization.getOrganizationAccountId();
        entity.ownerUserId = organization.getOwnerUserId();
        entity.applicationId = organization.getApplicationId();
        entity.businessLicenseFileId = organization.getBusinessLicenseFileId();
        entity.businessRegistrationNumber = organization.getBusinessRegistrationNumber();
        entity.businessName = organization.getBusinessName();
        entity.representativeName = organization.getRepresentativeName();
        entity.representativePhone = organization.getRepresentativePhone();
        entity.openingDate = organization.getOpeningDate();
        entity.roadAddress = organization.getRoadAddress();
        entity.jibunAddress = organization.getJibunAddress();
        entity.detailAddress = organization.getDetailAddress();
        entity.latitude = organization.getLatitude();
        entity.longitude = organization.getLongitude();
        entity.websiteUrl = organization.getWebsiteUrl();
        entity.instagramUrl = organization.getInstagramUrl();
        entity.blogUrl = organization.getBlogUrl();
        entity.facilityPhone = organization.getFacilityPhone();
        entity.status = organization.getStatus();
        return entity;
    }

    public Organization toDomain() {
        return Organization.restore(
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
                status,
                getCreatedAt()
        );
    }
}
