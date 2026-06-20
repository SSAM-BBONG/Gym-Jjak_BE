package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import org.springframework.stereotype.Component;

// PtCourse 도메인 ↔ JPA 엔티티 변환 매퍼
@Component
public class PtCoursePersistenceMapper {

    public PtCourseJpaEntity toEntity(PtCourse domain) {
        return new PtCourseJpaEntity(
                domain.getOrganizationId(),
                domain.getTrainerProfileId(),
                domain.getCategoryId(),
                domain.getTagId(),
                domain.getThumbnailFileId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getTotalSessionCount(),
                domain.getStatus()
        );
    }

    public PtCourse toDomain(PtCourseJpaEntity entity) {
        return PtCourse.restore(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getTrainerProfileId(),
                entity.getCategoryId(),
                entity.getTagId(),
                entity.getThumbnailFileId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getTotalSessionCount(),
                entity.getStatus()
        );
    }
}
