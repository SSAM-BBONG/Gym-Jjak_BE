package com.ssambbong.gymjjak.organization.organizationTrainer.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrganizationTrainerRetentionJob implements RetentionJob {

    private final OrganizationTrainerRetentionService organizationTrainerRetentionService;

    @Override
    public String name() {
        return OrganizationTrainerRetentionService.JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return organizationTrainerRetentionService.hardDeleteExpiredOrganizationTrainers(now);
    }
}
