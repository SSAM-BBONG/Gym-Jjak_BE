package com.ssambbong.gymjjak.organization.organization.infrastructure.metrics;

import com.ssambbong.gymjjak.organization.organization.application.port.OrganizationMetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMetrics implements OrganizationMetricsPort {

    private final Counter organizationCreatedCounter;
    private final Counter organizationUpdatedCounter;

    public OrganizationMetrics(MeterRegistry meterRegistry) {
        this.organizationCreatedCounter = Counter.builder("gymjjak.organization.created")
                .description("조직 생성 횟수")
                .register(meterRegistry);
        this.organizationUpdatedCounter = Counter.builder("gymjjak.organization.updated")
                .description("조직 정보 수정 횟수")
                .register(meterRegistry);
    }

    @Override
    public void recordOrganizationCreated() {
        organizationCreatedCounter.increment();
    }

    @Override
    public void recordOrganizationUpdated() {
        organizationUpdatedCounter.increment();
    }
}
