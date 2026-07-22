package com.ssambbong.gymjjak.trainerReport.infrastructure.persistence;

import com.ssambbong.gymjjak.trainerReport.domain.model.TrainerReport;
import org.springframework.stereotype.Component;

@Component
public class TrainerReportPersistenceMapper {

    public TrainerReportJpaEntity toEntity(TrainerReport domain) {
        return TrainerReportJpaEntity.builder()
                .id(domain.getId())
                .trainerProfileId(domain.getTrainerProfileId())
                .targetMonth(domain.getTargetMonth())
                .report(domain.getReport())
                .marketTrendsSnapshot(domain.getMarketTrendsSnapshot())
                .build();
    }

    public TrainerReport toDomain(TrainerReportJpaEntity entity) {
        return TrainerReport.restore(
                entity.getId(),
                entity.getTrainerProfileId(),
                entity.getTargetMonth(),
                entity.getReport(),
                entity.getMarketTrendsSnapshot(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
