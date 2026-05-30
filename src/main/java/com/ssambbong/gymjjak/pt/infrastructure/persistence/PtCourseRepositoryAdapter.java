package com.ssambbong.gymjjak.pt.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public List<PtCourse> findAllOrderByCreatedAtDesc() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PtCoursePage findAllVisible(Long categoryId, Long tagId, int page, int size) {
        Page<PtCourseJpaEntity> result = repository.findAllVisibleWithFilters(
                categoryId, tagId, PageRequest.of(page, size));
        return new PtCoursePage(
                result.getContent().stream().map(this::toDomain).toList(),
                result.getTotalElements()
        );
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
