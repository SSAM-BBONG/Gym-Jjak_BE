package com.ssambbong.gymjjak.organization.organizationTrainer.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class OrganizationTrainerAlreadyExistsException extends ConflictException {

    public OrganizationTrainerAlreadyExistsException() {
        super(OrganizationTrainerErrorCode.ORGANIZATION_TRAINER_ALREADY_EXISTS);
    }

    public OrganizationTrainerAlreadyExistsException(Throwable cause) {
        super(OrganizationTrainerErrorCode.ORGANIZATION_TRAINER_ALREADY_EXISTS,
                OrganizationTrainerErrorCode.ORGANIZATION_TRAINER_ALREADY_EXISTS.getMessage(), cause);
    }
}
