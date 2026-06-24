package com.ssambbong.gymjjak.organization.organizationTrainer.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrganizationTrainerErrorCode implements ErrorCode {

    ORGANIZATION_TRAINER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORG_OT_404", "소속 트레이너를 찾을 수 없습니다."),
    ORGANIZATION_TRAINER_ALREADY_EXISTS(HttpStatus.CONFLICT, "ORG_OT_409", "이미 소속된 트레이너입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
