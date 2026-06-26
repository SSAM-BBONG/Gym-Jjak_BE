package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    // id가 있는 스케줄 필드 수정 — 소유권 검증 후 dirty checking으로 UPDATE, 미존재 시 예외
    @Override
    public void update(PtCourseSchedule schedule) {
        PtCourseScheduleJpaEntity entity = repository
                .findByIdAndPtCourseId(schedule.getId(), schedule.getPtCourseId())
                .orElseThrow(PtCourseNotFoundException::new);
        entity.updateFields(schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime());
    }

    // upsert 시 요청에 없는 스케줄 일괄 삭제
    @Override
    public void deleteAllByIdIn(List<Long> ids) {
        if (ids.isEmpty()) return;
        repository.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional
    public int hardDeleteByPtCourseIds(List<Long> ptCourseIds) {
        if (ptCourseIds.isEmpty()) return 0; // 빈 IN절 쿼리 방지
        return repository.hardDeleteByPtCourseIds(ptCourseIds);
    }
}
