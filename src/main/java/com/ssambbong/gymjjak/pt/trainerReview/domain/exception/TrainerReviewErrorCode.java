package com.ssambbong.gymjjak.pt.trainerReview.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TrainerReviewErrorCode implements ErrorCode {

    TRAINER_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_001", "강사평을 찾을 수 없습니다."),
    TRAINER_REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "REVIEW_002", "해당 예약에 이미 강사평이 존재합니다."),
    TRAINER_REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "REVIEW_003", "접근 권한이 없습니다."),
    PT_RESERVATION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "REVIEW_004", "완료된 PT 예약에만 강사평을 작성할 수 있습니다."),
    PT_RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_005", "PT 예약을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
