package com.ssambbong.gymjjak.organization.scheduler.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationRetentionService {

    public static final String JOB_NAME = "organization-retention";

    private final OrganizationRetentionProperties properties;
    private final OrganizationTrainerRepository organizationTrainerRepository;
    private final OrganizationApplicationRepository organizationApplicationRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpired(LocalDateTime now) {
        int[] trainers = hardDeleteExpiredTrainers(now);
        int[] applications = hardDeleteExpiredApplications(now);
        int[] organizations = hardDeleteExpiredOrganizations(now);

        int totalCandidates = trainers[0] + applications[0] + organizations[0];
        int totalDeleted = trainers[1] + applications[1] + organizations[1];

        return new RetentionJobResult(JOB_NAME, totalCandidates, 0, totalDeleted);
    }

    private int[] hardDeleteExpiredTrainers(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);
        List<Long> candidateIds = organizationTrainerRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=organization-trainer-retention-empty threshold={}", threshold);
            return new int[]{0, 0};
        }

        int deleted = organizationTrainerRepository.hardDeleteByIds(candidateIds);
        log.info("event=organization-trainer-retention-completed threshold={}, candidateCount={}, deleted={}", threshold, candidateIds.size(), deleted);
        return new int[]{candidateIds.size(), deleted};
    }

    private int[] hardDeleteExpiredApplications(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);
        List<Long> candidateIds = organizationApplicationRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=organization-application-retention-empty threshold={}", threshold);
            return new int[]{0, 0};
        }

        int deleted = organizationApplicationRepository.hardDeleteByIds(candidateIds);
        log.info("event=organization-application-retention-completed threshold={}, candidateCount={}, deleted={}", threshold, candidateIds.size(), deleted);
        return new int[]{candidateIds.size(), deleted};
    }

    private int[] hardDeleteExpiredOrganizations(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);
        List<Long> candidateIds = organizationRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=organization-retention-empty threshold={}", threshold);
            return new int[]{0, 0};
        }

        List<Long> applicationIds = organizationRepository.findApplicationIdsByOrganizationIds(candidateIds);
        int deletedOrgs = organizationRepository.hardDeleteByIds(candidateIds);
        organizationApplicationRepository.hardDeleteByIds(applicationIds);

        log.info("event=organization-retention-completed threshold={}, candidateCount={}, deletedOrgs={}, deletedApplications={}", threshold, candidateIds.size(), deletedOrgs, applicationIds.size());
        return new int[]{candidateIds.size(), deletedOrgs};
    }
}
