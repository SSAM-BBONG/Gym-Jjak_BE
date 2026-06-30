package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;
import java.util.Map;

public interface OrganizationQueryPort {

    OrganizationInfo findById(Long organizationId);

    Map<Long, OrganizationInfo> findAllByIds(List<Long> ids);

    // trainerProfileId로 소속 조직 ID 조회 (PT 강습 생성 시)
    Long findOrganizationIdByTrainerProfileId(Long trainerProfileId);

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
