package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import java.util.List;

public interface TrainerApplicationOrganizationPort {

    // 활성화된 조직 확인 메서드
    long countActiveOrganizationsByIds(List<Long> organizationIds);

    // 조직의 userId로 organizationId 반환 메서드
    Long findOrganizationIdByAccountId(Long organizationAccountId);
}
