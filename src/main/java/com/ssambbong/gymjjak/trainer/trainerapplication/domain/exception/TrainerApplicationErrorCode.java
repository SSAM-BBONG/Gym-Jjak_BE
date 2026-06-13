package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TrainerApplicationErrorCode implements ErrorCode {

    TRAINER_APPLICATION_INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "TRAINER_APPLICATION_400_1",
            "트레이너 신청 요청값이 유효하지 않습니다."
    ),

    REQUIRED_CERTIFICATION_NOT_VERIFIED(
            HttpStatus.BAD_REQUEST,
            "TRAINER_APPLICATION_400_2",
            "필수 자격증 OCR 검증에 실패했습니다."
    ),

    TRAINER_APPLICATION_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "TRAINER_APPLICATION_409_1",
            "이미 처리 중이거나 승인된 트레이너 신청이 존재합니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
