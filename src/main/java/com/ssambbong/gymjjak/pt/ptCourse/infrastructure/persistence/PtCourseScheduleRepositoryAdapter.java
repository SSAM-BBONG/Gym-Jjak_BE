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

    // id가 있는 스케줄 필드 수정 — 더티체킹으로 UPDATE
    @Override
    public void update(PtCourseSchedule schedule) {
        repository.findById(schedule.getId()).ifPresent(entity ->
                entity.updateFields(schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime())
        );
    }

    // upsert 시 요청에 없는 스케줄 일괄 삭제
    @Override
    public void deleteAllByIdIn(List<Long> ids) {
        if (ids.isEmpty()) return;
        repository.deleteAllByIdIn(ids);
    }
}
