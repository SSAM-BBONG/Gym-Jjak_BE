package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PtCurriculumRepositoryAdapter implements PtCurriculumRepository {

    private final SpringDataPtCurriculumRepository repository;
    private final PtCurriculumPersistenceMapper mapper;

    @Override
    public List<PtCurriculum> saveAll(List<PtCurriculum> curriculums) {
        List<PtCurriculumJpaEntity> entities = curriculums.stream()
                .map(mapper::toEntity)
                .toList();

        return repository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PtCurriculum> findAllByPtCourseId(Long ptCourseId) {
        return repository.findAllByPtCourseIdOrderBySessionNoAsc(ptCourseId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<PtCurriculum> findById(Long ptCurriculumId) {
        return repository.findById(ptCurriculumId).map(mapper::toDomain);
    }

    @Override
    public Optional<PtCurriculum> findByIdAndPtCourseId(Long ptCurriculumId, Long ptCourseId) {
        return repository.findByIdAndPtCourseId(ptCurriculumId, ptCourseId).map(mapper::toDomain);
    }

    // id가 있는 커리큘럼 필드 수정 — 소유권 검증 후 dirty checking으로 UPDATE, 미존재 시 예외
    @Override
    public void update(PtCurriculum curriculum) {
        PtCurriculumJpaEntity entity = repository
                .findByIdAndPtCourseId(curriculum.getId(), curriculum.getPtCourseId())
                .orElseThrow(PtCourseNotFoundException::new);
        entity.updateFields(curriculum.getSessionNo(), curriculum.getTitle(), curriculum.getContent());
    }

    // upsert 시 요청에 없는 커리큘럼 일괄 삭제
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
