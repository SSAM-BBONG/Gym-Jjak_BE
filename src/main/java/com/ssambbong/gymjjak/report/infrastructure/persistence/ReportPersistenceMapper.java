package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.report.domain.model.Report;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ReportPersistenceMapper {

    ReportJpaEntity toEntity(Report report);

    default Report toDomain(ReportJpaEntity entity) {
        return Report.reconstruct(
                entity.getReportId(),
                entity.getReportGroupId(),
                entity.getReporterId(),
                entity.getReason(),
                entity.getDetail(),
                entity.getStatus(),
                entity.getProcessedBy(),
                entity.getProcessedAt(),
                entity.getCreatedAt()
        );
    }
}
