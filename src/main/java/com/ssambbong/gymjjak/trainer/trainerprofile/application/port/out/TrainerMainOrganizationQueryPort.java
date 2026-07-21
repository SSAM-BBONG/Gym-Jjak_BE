package com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out;

import java.util.List;
import java.util.Map;

public interface TrainerMainOrganizationQueryPort {

    // 트레이너의 활성 소속 조직 수 조회
    long countActiveOrganizations(Long trainerProfileId);

    // 조직id로 카드에 필요한 헬스장name 조회
    Map<Long, String> findOrganizationNamesByIds(List<Long> organizationIds);
}
