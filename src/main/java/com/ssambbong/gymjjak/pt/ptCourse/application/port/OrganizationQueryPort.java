package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;
import java.util.Map;

public interface OrganizationQueryPort {

    OrganizationInfo findById(Long organizationId);

    Map<Long, OrganizationInfo> findAllByIds(List<Long> ids);

    // 트레이너가 해당 조직에 소속되어 있는지 검증 (PT 강습 생성 시)
    boolean isTrainerBelongsToOrganization(Long trainerProfileId, Long organizationId);

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
