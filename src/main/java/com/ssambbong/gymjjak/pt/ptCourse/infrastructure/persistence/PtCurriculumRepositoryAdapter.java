package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
