package com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository;

import com.ssambbong.gymjjak.organization.organizationTrainer.domain.model.OrganizationTrainer;

import java.util.List;

public interface OrganizationTrainerRepository {

    // 조직의 활성 트레이너 목록 조회
    List<OrganizationTrainer> findActiveByOrganizationId(Long organizationId);

    // 조직의 활성 트레이너 수 조회
    long countActiveByOrganizationId(Long organizationId);
}
