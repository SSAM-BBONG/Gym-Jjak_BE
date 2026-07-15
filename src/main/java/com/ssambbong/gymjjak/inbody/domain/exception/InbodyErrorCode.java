package com.ssambbong.gymjjak.inbody.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InbodyErrorCode implements ErrorCode {

    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "INBODY_400_1", "사용자 ID는 필수입니다."),
    MEASURED_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "INBODY_400_2", "측정일은 필수입니다."),
    MEASURED_DATE_IN_FUTURE(HttpStatus.BAD_REQUEST, "INBODY_400_3", "미래 날짜로 인바디를 등록할 수 없습니다."),
    HEIGHT_REQUIRED(HttpStatus.BAD_REQUEST, "INBODY_400_4", "키는 필수입니다."),
    WEIGHT_REQUIRED(HttpStatus.BAD_REQUEST, "INBODY_400_5", "몸무게는 필수입니다."),
    INVALID_INBODY_VALUE(HttpStatus.BAD_REQUEST, "INBODY_400_6", "인바디 수치는 0 이상이어야 합니다."),
    DUPLICATE_MEASURED_DATE(HttpStatus.CONFLICT, "INBODY_409_1", "동일한 측정일의 인바디 기록이 이미 존재합니다."),
    INBODY_ID_REQUIRED(HttpStatus.BAD_REQUEST, "INBODY_400_7", "인바디 ID는 필수입니다."),
    UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "INBODY_400_8", "인바디 기록은 생성 당일에만 수정할 수 있습니다."),
    INBODY_NOT_FOUND(HttpStatus.NOT_FOUND, "INBODY_404_1", "인바디 기록을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
