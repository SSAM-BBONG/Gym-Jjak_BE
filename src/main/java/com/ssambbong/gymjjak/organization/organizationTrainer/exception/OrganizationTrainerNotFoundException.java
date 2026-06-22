package com.ssambbong.gymjjak.organization.organizationTrainer.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class OrganizationTrainerNotFoundException extends NotFoundException {

    public OrganizationTrainerNotFoundException() {
        super(OrganizationTrainerErrorCode.ORGANIZATION_TRAINER_NOT_FOUND);
    }
}
