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

    @Override
    public PtCourse save(PtCourse ptCourse) {
        PtCourseJpaEntity entity = new PtCourseJpaEntity(
                ptCourse.getOrganizationId(),
                ptCourse.getTrainerProfileId(),
                ptCourse.getCategoryId(),
                ptCourse.getTagId(),
                ptCourse.getThumbnailFileId(),
                ptCourse.getTitle(),
                ptCourse.getDescription(),
                ptCourse.getPrice(),
                ptCourse.getTotalSessionCount(),
                ptCourse.isSupportsDietLog(),
                ptCourse.isSupportsWorkoutLog(),
                ptCourse.getStatus()
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<PtCourse> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PtCourse> findAllVisible() {
        return repository.findAllByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(PtCourseStatus.VISIBLE)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void update(PtCourse ptCourse) {
        PtCourseJpaEntity entity = repository.findById(ptCourse.getId())
                .orElseThrow(PtCourseNotFoundException::new);

        if (ptCourse.getStatus() == PtCourseStatus.DELETED) {
            entity.softDelete(); // status=DELETED + deletedAt=now()
        } else {
            entity.updateStatus(ptCourse.getStatus()); // BLOCKED/VISIBLE 상태만 변경
        }
        // save() 없이 @Transactional 더티체킹으로 자동 UPDATE
    }

    private PtCourse toDomain(PtCourseJpaEntity entity) {
        return PtCourse.restore(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getTrainerProfileId(),
                entity.getCategoryId(),
                entity.getTagId(),
                entity.getThumbnailFileId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getTotalSessionCount(),
                entity.isSupportsDietLog(),
                entity.isSupportsWorkoutLog(),
                entity.getStatus()
        );
    }
}
