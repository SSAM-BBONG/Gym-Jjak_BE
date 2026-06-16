package com.ssambbong.gymjjak.organization.organizationApplication.application.endpoint;

import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "org-application")
public class OrgApplicationEndpoint {

    private final OrganizationApplicationRepository organizationApplicationRepository;

    public OrgApplicationEndpoint(OrganizationApplicationRepository organizationApplicationRepository) {
        this.organizationApplicationRepository = organizationApplicationRepository;
    }

    @ReadOperation
    public OrgApplicationSummary summary() {
        long total = organizationApplicationRepository.count();
        long pending = organizationApplicationRepository.countByStatus(OrganizationApplicationStatus.PENDING);
        long accepted = organizationApplicationRepository.countByStatus(OrganizationApplicationStatus.ACCEPTED);
        double approvalRate = total == 0 ? 0 : Math.round((double) accepted / total * 1000) / 10.0;

        return new OrgApplicationSummary(total, pending, accepted, approvalRate);
    }

    public record OrgApplicationSummary(
            long totalCount,
            long pendingCount,
            long acceptedCount,
            double approvalRate
    ) {}
}
