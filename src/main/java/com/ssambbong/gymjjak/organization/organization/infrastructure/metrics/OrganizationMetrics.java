package com.ssambbong.gymjjak.organization.organization.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMetrics {

    private final Counter organizationCreatedCounter;

    public OrganizationMetrics(MeterRegistry meterRegistry) {
        this.organizationCreatedCounter = Counter.builder("gymjjak.organization.created")
                .description("조직 생성 횟수")
                .register(meterRegistry);
    }

    public void recordOrganizationCreated() {
        organizationCreatedCounter.increment();
    }
}
