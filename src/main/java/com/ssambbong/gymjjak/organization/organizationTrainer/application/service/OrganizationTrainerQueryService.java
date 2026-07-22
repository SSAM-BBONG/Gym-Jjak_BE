package com.ssambbong.gymjjak.organization.organizationTrainer.application.service;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organization.infrastructure.persistence.SpringDataOrganizationRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.MyOrganizationResult;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase.OrganizationTrainerQueryUseCase;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence.SpringDataOrganizationTrainerRepository;
import com.ssambbong.gymjjak.global.infrastructure.aop.Monitored;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationTrainerQueryService implements OrganizationTrainerQueryUseCase {

    private final OrganizationTrainerRepository organizationTrainerRepository;
    private final OrganizationRepository organizationRepository;
    private final TrainerProfileQueryPort trainerProfileQueryPort;
    private final SpringDataOrganizationTrainerRepository springDataOrganizationTrainerRepository;
    private final SpringDataOrganizationRepository springDataOrganizationRepository;

    @Monitored(name = "gymjjak.org.trainer.query.duration", domain = "org_trainer", action = "find_all")
    @Override
    public List<TrainerSummary> findMyOrganizationTrainers(Long organizationAccountId) {
        Organization organization = organizationRepository.findByOrganizationAccountId(organizationAccountId)
                .orElseThrow(OrganizationNotFoundException::new);
        return organizationTrainerRepository.findTrainersByOrganizationId(organization.getOrganizationId());
    }

    // 소속 조직 조회
    @Override
    public List<MyOrganizationResult> findMyOrganizations(Long userId) {
        Long trainerProfileId = trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(userId);
        List<Long> orgIds = springDataOrganizationTrainerRepository
                .findAllByTrainerProfileIdAndRemovedAtIsNull(trainerProfileId)
                .stream()
                .map(e -> e.getOrganizationId())
                .toList();
        if (orgIds.isEmpty()) {
            return List.of();
        }
        return springDataOrganizationRepository.findAllById(orgIds).stream()
                .map(o -> new MyOrganizationResult(o.getOrganizationId(), o.getBusinessName(), o.getRoadAddress()))
                .toList();
    }
}
