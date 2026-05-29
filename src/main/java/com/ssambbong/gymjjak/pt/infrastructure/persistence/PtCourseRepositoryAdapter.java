package com.ssambbong.gymjjak.pt.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PtCourseRepositoryAdapter implements PtCourseRepository {

    private final SpringDataPtCourseRepository repository;

    // pt 등록
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

    // pt 상세 조회
    @Override
    public Optional<PtCourse> findById(Long id) {
        return repository.findById(id)
                .map(entity -> toDomain(entity));
    }

    // pt 목록 조회
    @Override
    public List<PtCourse> findAllOrderByCreatedAtDesc() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    // JpaEntity → Domain 변환. RepositoryAdapter가 변환 책임 담당
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
