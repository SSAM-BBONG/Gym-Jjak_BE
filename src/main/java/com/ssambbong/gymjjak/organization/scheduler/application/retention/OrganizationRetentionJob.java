package com.ssambbong.gymjjak.organization.scheduler.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrganizationRetentionJob implements RetentionJob {

    private final OrganizationRetentionService organizationRetentionService;

    @Override
    public String name() {
        return OrganizationRetentionService.JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return organizationRetentionService.hardDeleteExpired(now);
    }
}
