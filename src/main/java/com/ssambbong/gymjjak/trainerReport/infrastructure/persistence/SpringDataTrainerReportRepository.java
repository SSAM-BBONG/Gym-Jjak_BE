package com.ssambbong.gymjjak.trainerReport.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataTrainerReportRepository extends JpaRepository<TrainerReportJpaEntity, Long> {

    Optional<TrainerReportJpaEntity> findByTrainerProfileIdAndTargetMonth(Long trainerProfileId, LocalDate targetMonth);

    List<TrainerReportJpaEntity> findAllByTrainerProfileIdOrderByTargetMonthDesc(Long trainerProfileId, Pageable pageable);

    Optional<TrainerReportJpaEntity> findByIdAndTrainerProfileId(Long id, Long trainerProfileId);
}
