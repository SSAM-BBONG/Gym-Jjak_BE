package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PtCurriculumRepositoryAdapter implements PtCurriculumRepository {

    private final SpringDataPtCurriculumRepository repository;

    @Override
    public List<PtCurriculum> saveAll(List<PtCurriculum> curriculums) {
        List<PtCurriculumJpaEntity> entities = curriculums.stream()
                .map(c -> new PtCurriculumJpaEntity(
                        c.getPtCourseId(), c.getSessionNo(), c.getTitle(), c.getContent()
                ))
                .toList();

        return repository.saveAll(entities).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<PtCurriculum> findAllByPtCourseId(Long ptCourseId) {
        return repository.findAllByPtCourseIdOrderBySessionNoAsc(ptCourseId).stream()
                .map(this::toDomain)
                .toList();
    }

    private PtCurriculum toDomain(PtCurriculumJpaEntity entity) {
        return PtCurriculum.restore(
                entity.getId(),
                entity.getPtCourseId(),
                entity.getSessionNo(),
                entity.getTitle(),
                entity.getContent()
        );
    }
}
