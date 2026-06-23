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
    public Optional<PtCourse> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id).map(mapper::toDomain);
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

        // 강습 필드 수정 (제목·설명·카테고리·태그·가격·썸네일·총 회차)
        entity.updateFields(
                ptCourse.getCategoryId(),
                ptCourse.getTagId(),
                ptCourse.getThumbnailFileId(),
                ptCourse.getTitle(),
                ptCourse.getDescription(),
                ptCourse.getPrice(),
                ptCourse.getTotalSessionCount()
        );

        if (ptCourse.getStatus() == PtCourseStatus.DELETED) {
            entity.softDelete(); // status=DELETED + deletedAt=now()
        } else {
            entity.updateStatus(ptCourse.getStatus()); // VISIBLE/HIDDEN/BLOCKED 상태 변경
        }
        // save() 없이 @Transactional 더티체킹으로 자동 UPDATE
    }

    @Override
    public List<PtCourse> findAllByTrainerProfileId(Long trainerProfileId, PtCourseStatus status) {
        List<PtCourseJpaEntity> entities = (status == null)
                // status 미지정 → VISIBLE + HIDDEN만 (BLOCKED, DELETED 제외, soft delete 안전)
                ? repository.findAllByTrainerProfileIdAndStatusInAndDeletedAtIsNullOrderByCreatedAtDesc(
                trainerProfileId, List.of(PtCourseStatus.VISIBLE, PtCourseStatus.HIDDEN))
                : repository.findAllByTrainerProfileIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                trainerProfileId, status);

        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
