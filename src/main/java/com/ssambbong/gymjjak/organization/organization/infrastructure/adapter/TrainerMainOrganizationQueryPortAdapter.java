package com.ssambbong.gymjjak.organization.organization.infrastructure.adapter;

import com.ssambbong.gymjjak.organization.organization.infrastructure.persistence.SpringDataOrganizationRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence.SpringDataOrganizationTrainerRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerMainOrganizationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainerMainOrganizationQueryPortAdapter implements TrainerMainOrganizationQueryPort {

    private final SpringDataOrganizationTrainerRepository organizationTrainerRepository;
    private final SpringDataOrganizationRepository organizationRepository;

    @Override
    // removedAt이 없는 소속 관계의 조직 ID를 중복 없이 집계
    public long countActiveOrganizations(Long trainerProfileId) {
        return organizationTrainerRepository
                .countDistinctActiveOrganizationsByTrainerProfileId(trainerProfileId);
    }

    @Override
    // 카드 헬스장명은 organizationId 목록 조회
    public Map<Long, String> findOrganizationNamesByIds(List<Long> organizationIds) {
        if (organizationIds.isEmpty()) {
            return Map.of();
        }

        return organizationRepository.findAllById(organizationIds).stream()
                .collect(Collectors.toMap(
                        organization -> organization.getOrganizationId(),
                        organization -> organization.getBusinessName(),
                        (existing, ignored) -> existing
                ));
    }
}
