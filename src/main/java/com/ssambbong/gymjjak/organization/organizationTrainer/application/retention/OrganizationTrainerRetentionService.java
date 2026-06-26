package com.ssambbong.gymjjak.organization.organizationTrainer.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
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
public class OrganizationTrainerRetentionService {

    public static final String JOB_NAME = "organization-trainer-retention";

    private final OrganizationTrainerRetentionProperties properties;
    private final OrganizationTrainerRepository organizationTrainerRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpiredOrganizationTrainers(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);

        List<Long> candidateIds = organizationTrainerRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=organization-trainer-retention-empty threshold={}", threshold);
            return RetentionJobResult.empty(JOB_NAME);
        }

        int deleted = organizationTrainerRepository.hardDeleteByIds(candidateIds);

        log.info("event=organization-trainer-retention-completed threshold={}, candidateCount={}, deleted={}",
                threshold, candidateIds.size(), deleted);

        return new RetentionJobResult(JOB_NAME, candidateIds.size(), 0, deleted);
    }
}
