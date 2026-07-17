package com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase;

import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.MyOrganizationResult;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerSummary;

import java.util.List;

public interface OrganizationTrainerQueryUseCase {

    List<TrainerSummary> findMyOrganizationTrainers(Long organizationAccountId);

    List<MyOrganizationResult> findMyOrganizations(Long userId);
}
