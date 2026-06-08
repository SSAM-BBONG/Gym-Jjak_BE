package com.ssambbong.gymjjak.organization.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrgApplicationMetrics {

    private final Counter orgApplicationCreatedCount;
    private final Counter orgApplicationApprovedCount;
    private final Counter orgApplicationRejectedCount;


    public OrgApplicationMetrics(MeterRegistry meterRegistry) {

        this.orgApplicationCreatedCount = Counter.builder("gymjjak.organization.application.created")
                .description("조직 신청 생성 횟수")
                .register(meterRegistry);

        this.orgApplicationApprovedCount = Counter.builder("gymjjak.organization.application.approved")
                .description("조직 신청 승인 횟수")
                .register(meterRegistry);

        this.orgApplicationRejectedCount = Counter.builder("gymjjak.organization.application.rejected")
                .description("조직 신청 반려 횟수")
                .register(meterRegistry);

    }

    public void recordOrgApplicationCreated() {
        orgApplicationCreatedCount.increment();
    }

    public void recordOrgApplicationApproved() {
        orgApplicationApprovedCount.increment();
    }

    public void recordOrgApplicationRejected() {
        orgApplicationRejectedCount.increment();
    }

}
