package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.report.domain.model.Report;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ReportPersistenceMapper {

    default ReportJpaEntity toEntity(Report report) {
        if (report == null) {
            return null;
        }

        return ReportJpaEntity.of(
                report.getReportId(),
                report.getReportGroupId(),
                report.getReporterId(),
                report.getReason(),
                report.getDetail(),
                report.getStatus(),
                report.getProcessedBy(),
                report.getProcessedAt()
        );
    }

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
