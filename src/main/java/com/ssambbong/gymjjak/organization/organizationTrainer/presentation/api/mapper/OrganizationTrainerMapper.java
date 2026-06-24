package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.command.AddOrganizationTrainerCommand;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.request.AddOrganizationTrainerRequest;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response.FindOrganizationTrainerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface OrganizationTrainerMapper {

    @Mapping(source = "organizationAccountId", target = "organizationAccountId")
    @Mapping(source = "request.trainerProfileId", target = "trainerProfileId")
    AddOrganizationTrainerCommand toCommand(AddOrganizationTrainerRequest request, Long organizationAccountId);

    FindOrganizationTrainerResponse toResponse(TrainerSummary summary);

    List<FindOrganizationTrainerResponse> toResponseList(List<TrainerSummary> summaries);
}
