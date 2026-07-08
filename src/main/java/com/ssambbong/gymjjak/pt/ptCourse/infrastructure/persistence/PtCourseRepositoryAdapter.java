package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

        entity.updateFields(
                ptCourse.getPartId(),
                ptCourse.getThumbnailFileId(),
                ptCourse.getTitle(),
                ptCourse.getDescription(),
                ptCourse.getPrice(),
                ptCourse.getTotalSessionCount()
        );

        if (ptCourse.getStatus() == PtCourseStatus.DELETED) {
            // 도메인에서 결정한 deletedAt을 그대로 반영
            entity.softDelete(ptCourse.getDeletedAt());
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

    // 인기 강습 조회
    @Override
    public List<PtCourse> findPopular(int limit) {
        return repository.findPopular(limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Long> findHardDeleteCandidateIds(java.time.LocalDateTime threshold, int batchSize) {
        return repository.findHardDeleteCandidateIds(threshold, batchSize);
    }

    @Override
    @Transactional
    public int hardDeleteByIds(List<Long> ids) {
        if (ids.isEmpty()) return 0; // 빈 IN절 쿼리 방지
        return repository.hardDeleteByIds(ids);
    }
}
