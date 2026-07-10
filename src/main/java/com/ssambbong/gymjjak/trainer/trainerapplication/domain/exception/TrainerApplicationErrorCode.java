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

    TRAINER_APPLICATION_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "TRAINER_APPLICATION_403_1",
            "본인의 트레이너 신청서만 수정할 수 있습니다."
    ),

    TRAINER_APPLICATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TRAINER_APPLICATION_404_1",
            "트레이너 신청서를 찾을 수 없습니다."
    ),

    TRAINER_APPLICATION_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "TRAINER_APPLICATION_409_1",
            "이미 처리 중이거나 승인된 트레이너 신청이 존재합니다."
    ),

    TRAINER_APPLICATION_STATUS_CONFLICT(
            HttpStatus.CONFLICT,
        "TRAINER_APPLICATION_409_2",
                "PENDING 상태의 트레이너 신청서만 처리할 수 있습니다."
    ),

    TRAINER_APPLICATION_CANCEL_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "TRAINER_APPLICATION_403_2",
            "본인의 트레이너 신청서만 취소할 수 있습니다."
    ),

    TRAINER_APPLICATION_REVIEW_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "TRAINER_APPLICATION_403_3",
            "해당 조직의 트레이너 신청서만 승인 또는 반려할 수 있습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
