package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.port.out.TrainerProfileSearchQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerCondition;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerListResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.SearchTrainerResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainerProfileSearchQueryAdapter implements TrainerProfileSearchQueryPort {

    private final SpringDataTrainerProfileRepository repository;

    @Override
    public SearchTrainerListResult searchTrainers(SearchTrainerCondition condition) {
        PageRequest pageRequest = PageRequest.of(
                condition.page(),
                condition.size()
        );

        Slice<SpringDataTrainerProfileRepository.SearchTrainerRow> page =
                repository.searchTrainers(
                        TrainerProfileStatus.ACTIVE.name(),
                        condition.keyword(),
                        pageRequest
                );

        List<SearchTrainerResult> content = page.getContent()
                .stream()
                .map(this::toResult)
                .toList();

        return new SearchTrainerListResult(
                content,
                page.getNumber(),
                page.getSize(),
                page.hasNext()
        );
    }

    private SearchTrainerResult toResult(
            SpringDataTrainerProfileRepository.SearchTrainerRow row
    ) {
        return new SearchTrainerResult(
                row.getTrainerProfileId(),
                row.getName(),
                row.getUsername(),
                row.getNickname()
        );
    }
}
