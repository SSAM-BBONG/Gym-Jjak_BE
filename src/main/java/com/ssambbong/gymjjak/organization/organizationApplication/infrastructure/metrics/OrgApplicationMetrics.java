package com.ssambbong.gymjjak.organization.organizationApplication.infrastructure.metrics;

import com.ssambbong.gymjjak.organization.organizationApplication.application.port.OrgApplicationMetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrgApplicationMetrics implements OrgApplicationMetricsPort {

    private final Counter orgApplicationCreatedCount;
    private final Counter orgApplicationApprovedCount;
    private final Counter orgApplicationRejectedCount;
    private final Counter orgApplicationCancelledCount;

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
        this.orgApplicationCancelledCount = Counter.builder("gymjjak.organization.application.cancelled")
                .description("조직 신청 취소 횟수")
                .register(meterRegistry);
    }

    @Override
    public void recordOrgApplicationCreated() {
        orgApplicationCreatedCount.increment();
    }

    @Override
    public void recordOrgApplicationApproved() {
        orgApplicationApprovedCount.increment();
    }

    @Override
    public void recordOrgApplicationRejected() {
        orgApplicationRejectedCount.increment();
    }

    @Override
    public void recordOrgApplicationCancelled() {
        orgApplicationCancelledCount.increment();
    }
}
