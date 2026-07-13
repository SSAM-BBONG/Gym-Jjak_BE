package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationNotFoundException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationStatusInvalidException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public int bulkCancelByUserIdAndPtCourseId(Long userId, Long ptCourseId) {
        return repository.bulkCancelByUserIdAndPtCourseId(userId, ptCourseId);
    }

    @Override
    public int bulkCompleteByUserIdAndPtCourseId(Long userId, Long ptCourseId) {
        return repository.bulkCompleteByUserIdAndPtCourseId(userId, ptCourseId);
    }

    @Override
    public int countConsumedByUserIdAndPtCourseId(Long userId, Long ptCourseId) {
        return repository.countConsumedByUserIdAndPtCourseId(userId, ptCourseId);
    }

    @Override
    public int countProgressByUserIdAndPtCourseId(Long userId, Long ptCourseId) {
        return repository.countProgressByUserIdAndPtCourseId(userId, ptCourseId);
    }

    // 진행 중인 PT 수
    @Override
    public long countByStatus(PtReservationStatus status) {
        return repository.countByStatus(status);
    }

    @Override
    public List<LocalDateTime> findReservedStartAtsByPtCourseId(Long ptCourseId, LocalDateTime from, LocalDateTime to) {
        return repository.findReservedStartAtsByPtCourseIdAndRange(ptCourseId, from, to);
    }

    // AdminDashboard - 월별 예약된 pt 수 조회
    @Override
    public List<MonthlyReservationCount> findMonthlyReservationCounts(
            PtReservationStatus excludedStatus,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return repository.findMonthlyPtReservations(
                        excludedStatus.name(),
                        startDate,
                        endDate
                )
                .stream()
                .map(row -> new MonthlyReservationCount(
                        row.getMonth(),
                        row.getCount() == null ? 0L : row.getCount()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void updateStatus(PtReservation ptReservation) {
        PtReservationJpaEntity entity = repository.findById(ptReservation.getId())
                .orElseThrow(PtReservationNotFoundException::new);
        if (entity.getCancelledAt() != null || entity.getCompletedAt() != null) {
            throw new PtReservationStatusInvalidException();
        }
        entity.updateStatus(ptReservation.getStatus(), ptReservation.getCancelledAt(), ptReservation.getCompletedAt());
    }
}
