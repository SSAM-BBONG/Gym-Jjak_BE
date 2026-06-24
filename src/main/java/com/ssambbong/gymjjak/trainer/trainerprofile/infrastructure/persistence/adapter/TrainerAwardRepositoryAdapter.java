package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerAward;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerAwardRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerAwardJpaEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.mapper.TrainerAwardPersistenceMapper;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerAwardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainerAwardRepositoryAdapter implements TrainerAwardRepository {

    private final SpringDataTrainerAwardRepository repository;
    private final TrainerAwardPersistenceMapper mapper;


    @Override
    public void saveAll(List<TrainerAward> trainerAwards) {
        List<TrainerAwardJpaEntity> entities = trainerAwards.stream()
                .map(mapper::toEntity)
                .toList();

        repository.saveAll(entities);
    }

    @Override
    public List<TrainerAward> findAllByTrainerProfileId(Long trainerProfileId) {
        return repository.findAllByTrainerProfileIdOrderByTrainerAwardIdAsc(trainerProfileId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByTrainerProfileId(Long trainerProfileId) {
        repository.deleteAllByTrainerProfileId(trainerProfileId);
    }
}
