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
    private final PtCourseSchedulePersistenceMapper mapper;

    @Override
    public List<PtCourseSchedule> saveAll(List<PtCourseSchedule> schedules) {
        List<PtCourseScheduleJpaEntity> entities = schedules.stream()
                .map(mapper::toEntity)
                .toList();

        return repository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PtCourseSchedule> findAllByPtCourseId(Long ptCourseId) {
        return repository.findAllByPtCourseId(ptCourseId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
