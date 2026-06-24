package com.ssambbong.gymjjak.pt.ptCourse.application.port;

public interface OrganizationQueryPort {

    OrganizationInfo findById(Long organizationId);

    // 통계 -> 활성화 된 등록된 헬스장 수
    long countActive();

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
