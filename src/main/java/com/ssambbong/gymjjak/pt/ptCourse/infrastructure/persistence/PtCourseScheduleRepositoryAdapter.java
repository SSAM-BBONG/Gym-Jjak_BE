package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PtCourseScheduleRepositoryAdapter implements PtCourseScheduleRepository {

    private final SpringDataPtCourseScheduleRepository repository;

    @Override
    public List<PtCourseSchedule> saveAll(List<PtCourseSchedule> schedules) {
        List<PtCourseScheduleJpaEntity> entities = schedules.stream()
                .map(s -> new PtCourseScheduleJpaEntity(
                        s.getPtCourseId(), s.getDayOfWeek(), s.getStartTime(), s.getEndTime()
                ))
                .toList();

        return repository.saveAll(entities).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<PtCourseSchedule> findAllByPtCourseId(Long ptCourseId) {
        return repository.findAllByPtCourseId(ptCourseId).stream()
                .map(this::toDomain)
                .toList();
    }

    private PtCourseSchedule toDomain(PtCourseScheduleJpaEntity entity) {
        return PtCourseSchedule.restore(
                entity.getId(),
                entity.getPtCourseId(),
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }
}
