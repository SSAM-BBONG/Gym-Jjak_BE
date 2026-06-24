package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerProfileSearchQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerCondition;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerListResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TrainerProfileSearchQueryAdapter implements TrainerProfileSearchQueryPort {

    private final SpringDataTrainerProfileRepository repository;

    @Override
    public SearchTrainerListResult searchTrainers(SearchTrainerCondition condition) {


        PageRequest pageRequest = PageRequest.of(
                condition.page(), condition.size()
        );

        Page<SearchTrainerResult> page = repository.searchTrainers(
                TrainerProfileStatus.ACTIVE,
                condition.keyword(),
                pageRequest
        );

        return new SearchTrainerListResult(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
