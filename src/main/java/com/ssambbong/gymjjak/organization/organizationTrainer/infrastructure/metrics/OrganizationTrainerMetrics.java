package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.metrics;

import com.ssambbong.gymjjak.organization.organizationTrainer.application.port.OrganizationTrainerMetricsPort;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrganizationTrainerMetrics implements OrganizationTrainerMetricsPort {

    private final Counter trainerRegisteredCounter;
    private final Counter trainerRemovedCounter;

    public OrganizationTrainerMetrics(MeterRegistry meterRegistry,
                                      OrganizationTrainerRepository organizationTrainerRepository) {
        this.trainerRegisteredCounter = Counter.builder("gymjjak.organization.trainer.registered")
                .description("조직 트레이너 등록 횟수")
                .register(meterRegistry);

        this.trainerRemovedCounter = Counter.builder("gymjjak.organization.trainer.removed")
                .description("조직 트레이너 삭제 횟수")
                .register(meterRegistry);

        // scrape 시마다 DB 조회 — 데이터 증가 시 성능 주의
        Gauge.builder("gymjjak.organization.trainer.active.total", organizationTrainerRepository,
                        OrganizationTrainerRepository::countAllActive)
                .description("전체 활성 트레이너 수")
                .register(meterRegistry);

        Gauge.builder("gymjjak.organization.trainer.active.average_per_org", organizationTrainerRepository,
                        repo -> {
                            long orgs = repo.countActiveOrganizations();
                            return orgs == 0 ? 0 : (double) repo.countAllActive() / orgs;
                        })
                .description("조직별 평균 활성 트레이너 수")
                .register(meterRegistry);
    }

    @Override
    public void recordTrainerRegistered() {
        trainerRegisteredCounter.increment();
    }

    @Override
    public void recordTrainerRemoved() {
        trainerRemovedCounter.increment();
    }
}
