package com.ssambbong.gymjjak.pt.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PtReservationErrorCode implements ErrorCode {

    // 예약 필드 유효성 검증 실패 (시간 null, 종료시간이 시작시간보다 앞)
    PT_RESERVATION_INVALID(HttpStatus.BAD_REQUEST, "PT_RESERVATION_001", "예약 정보가 유효하지 않습니다."),

    // 예약 조회 실패
    PT_RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "PT_RESERVATION_002", "예약을 찾을 수 없습니다."),

    // 중복 예약
    PT_RESERVATION_DUPLICATE(HttpStatus.CONFLICT, "PT_RESERVATION_003", "이미 예약된 시간입니다."),

    // 본인 예약이 아닌 경우
    PT_RESERVATION_FORBIDDEN(HttpStatus.FORBIDDEN, "PT_RESERVATION_004", "본인의 예약만 조회할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
