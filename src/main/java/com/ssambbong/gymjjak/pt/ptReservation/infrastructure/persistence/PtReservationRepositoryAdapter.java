package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PtReservationRepositoryAdapter implements PtReservationRepository {

    private final SpringDataPtReservationRepository repository;
    private final PtReservationPersistenceMapper mapper;

    @Override
    public PtReservation save(PtReservation ptReservation) {
        PtReservationJpaEntity entity = new PtReservationJpaEntity(
                ptReservation.getUserId(),
                ptReservation.getPtCourseId(),
                ptReservation.getOrganizationId(),
                ptReservation.getTrainerProfileId(),
                ptReservation.getReservedStartAt(),
                ptReservation.getReservedEndAt(),
                ptReservation.getCancelledAt(),
                ptReservation.getCompletedAt(),
                ptReservation.getProgressCount(),
                ptReservation.getTotalSessionCount(),
                ptReservation.getStatus()
        );
        return mapper.toDomain((PtReservationJpaEntity) repository.save(entity));
    }

    @Override
    public boolean existsByPtCourseIdAndTimeOverlap(
            Long ptCourseId,
            LocalDateTime reservedStartAt,
            LocalDateTime reservedEndAt
    ) {
        return repository.existsOverlappingReservation(ptCourseId, reservedStartAt, reservedEndAt);
    }

    @Override
    public List<PtReservation> findAllByUserId(Long userId, PtReservationStatus status) {
        List<PtReservationJpaEntity> entities = (status == null)
                ? repository.findAllByUserIdOrderByReservedStartAtDesc(userId)
                : repository.findAllByUserIdAndStatusOrderByReservedStartAtDesc(userId, status);

        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<PtReservation> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    // 강습별 수강생 목록 조회
    @Override
    public List<PtReservation> findAllByPtCourseId(Long ptCourseId) {
        return repository.findAllByPtCourseIdOrderByReservedStartAtDesc(ptCourseId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
