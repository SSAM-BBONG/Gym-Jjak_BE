package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;
import org.springframework.stereotype.Component;

// PtCourseSchedule 도메인 ↔ JPA 엔티티 변환 매퍼
@Component
public class PtCourseSchedulePersistenceMapper {

    public PtCourseScheduleJpaEntity toEntity(PtCourseSchedule domain) {
        return new PtCourseScheduleJpaEntity(
                domain.getPtCourseId(),
                domain.getDayOfWeek(),
                domain.getStartTime(),
                domain.getEndTime()
        );
    }

    public PtCourseSchedule toDomain(PtCourseScheduleJpaEntity entity) {
        return PtCourseSchedule.restore(
                entity.getId(),
                entity.getPtCourseId(),
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }
}
