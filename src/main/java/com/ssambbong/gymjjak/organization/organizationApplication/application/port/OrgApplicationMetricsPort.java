package com.ssambbong.gymjjak.organization.organizationApplication.application.port;

public interface OrgApplicationMetricsPort {

    void recordOrgApplicationCreated();

    void recordOrgApplicationApproved();

    void recordOrgApplicationRejected();

    void recordOrgApplicationCancelled();
}
