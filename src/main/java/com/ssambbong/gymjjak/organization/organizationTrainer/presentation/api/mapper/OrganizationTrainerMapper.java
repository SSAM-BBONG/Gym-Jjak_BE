package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository.TrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response.FindOrganizationTrainerResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface OrganizationTrainerMapper {

    FindOrganizationTrainerResponse toResponse(TrainerSummary summary);

    List<FindOrganizationTrainerResponse> toResponseList(List<TrainerSummary> summaries);
}
