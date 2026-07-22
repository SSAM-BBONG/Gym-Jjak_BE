package com.ssambbong.gymjjak.trainerReport.infrastructure.persistence;

import com.ssambbong.gymjjak.trainerReport.domain.model.TrainerReport;
import com.ssambbong.gymjjak.trainerReport.domain.repository.TrainerReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerReportRepositoryAdapter implements TrainerReportRepository {

    private final SpringDataTrainerReportRepository repository;
    private final TrainerReportPersistenceMapper mapper;

    @Override
    public Long save(TrainerReport trainerReport) {
        return repository.save(mapper.toEntity(trainerReport)).getId();
    }

    @Override
    public Optional<TrainerReport> findByTrainerProfileIdAndTargetMonth(Long trainerProfileId, LocalDate targetMonth) {
        return repository.findByTrainerProfileIdAndTargetMonth(trainerProfileId, targetMonth)
                .map(mapper::toDomain);
    }

    @Override
    public List<TrainerReport> findAllByTrainerProfileId(Long trainerProfileId, int page, int size) {
        return repository.findAllByTrainerProfileIdOrderByTargetMonthDesc(trainerProfileId, PageRequest.of(page, size))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<TrainerReport> findByIdAndTrainerProfileId(Long id, Long trainerProfileId) {
        return repository.findByIdAndTrainerProfileId(id, trainerProfileId)
                .map(mapper::toDomain);
    }
}
