package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ReportGroupPersistenceMapper {

    ReportGroupJpaEntity toEntity(ReportGroup reportGroup);

    default ReportGroup toDomain(ReportGroupJpaEntity entity) {
        return ReportGroup.reconstruct(
                entity.getReportGroupId(),
                entity.getReportNumber(),
                entity.getTargetType(),
                entity.getTargetId(),
                entity.getTargetOwnerId(),
                entity.getSnapshotTitle(),
                entity.getSnapshotContent(),
                entity.getSnapshotFileUrl(),
                entity.getTotalReportCount(),
                entity.getEffectiveReportCount(),
                entity.getReviewStatus(),
                entity.getSanctionStatus(),
                entity.getProcessedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
