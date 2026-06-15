package com.ssambbong.gymjjak.pt.ptCourse.application.port;

public interface OrganizationQueryPort {

    OrganizationInfo findById(Long organizationId);

    record OrganizationInfo(
            Long organizationId,
            String businessName,
            String roadAddress,
            Double latitude,
            Double longitude,
            String phone,
            String websiteUrl,
            String instagramUrl
    ) {}
}
