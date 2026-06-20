package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;
import org.springframework.stereotype.Component;

// PtCurriculum 도메인 ↔ JPA 엔티티 변환 매퍼.
@Component
public class PtCurriculumPersistenceMapper {

    public PtCurriculumJpaEntity toEntity(PtCurriculum domain) {
        return new PtCurriculumJpaEntity(
                domain.getPtCourseId(),
                domain.getSessionNo(),
                domain.getTitle(),
                domain.getContent()
        );
    }

    public PtCurriculum toDomain(PtCurriculumJpaEntity entity) {
        return PtCurriculum.restore(
                entity.getId(),
                entity.getPtCourseId(),
                entity.getSessionNo(),
                entity.getTitle(),
                entity.getContent()
        );
    }
}
