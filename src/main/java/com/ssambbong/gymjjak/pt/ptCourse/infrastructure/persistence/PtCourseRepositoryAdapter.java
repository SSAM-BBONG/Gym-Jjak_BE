package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PtCourseRepositoryAdapter implements PtCourseRepository {

    private final SpringDataPtCourseRepository repository;
    private final PtCoursePersistenceMapper mapper;

    @Override
    public PtCourse save(PtCourse ptCourse) {
        return mapper.toDomain(repository.save(mapper.toEntity(ptCourse)));
    }

    @Override
    public Optional<PtCourse> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PtCourse> findAllVisible() {
        return repository.findAllByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(PtCourseStatus.VISIBLE)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void update(PtCourse ptCourse) {
        PtCourseJpaEntity entity = repository.findById(ptCourse.getId())
                .orElseThrow(PtCourseNotFoundException::new);

        if (ptCourse.getStatus() == PtCourseStatus.DELETED) {
            entity.softDelete(); // status=DELETED + deletedAt=now()
        } else {
            entity.updateStatus(ptCourse.getStatus()); // VISIBLE/BLOCKED/VISIBLE 상태 변경
        }
        // save() 없이 @Transactional 더티체킹으로 자동 UPDATE
    }
}
