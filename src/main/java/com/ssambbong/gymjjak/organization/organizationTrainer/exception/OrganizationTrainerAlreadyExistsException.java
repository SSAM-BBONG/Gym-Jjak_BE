package com.ssambbong.gymjjak.organization.organizationTrainer.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class OrganizationTrainerAlreadyExistsException extends ConflictException {

    public OrganizationTrainerAlreadyExistsException() {
        super(OrganizationTrainerErrorCode.ORGANIZATION_TRAINER_ALREADY_EXISTS);
    }
}
