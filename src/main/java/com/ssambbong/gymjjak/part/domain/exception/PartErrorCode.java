package com.ssambbong.gymjjak.part.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PartErrorCode implements ErrorCode {

    PART_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "PART_001", "입력된 이름이 없습니다."),
    PART_ALREADY_EXISTS(HttpStatus.CONFLICT, "PART_002", "이미 존재하는 부위입니다."),
    PART_NOT_FOUND(HttpStatus.NOT_FOUND, "PART_003", "부위를 찾을 수 없습니다."),
    PART_IN_USE(HttpStatus.CONFLICT, "PART_004", "사용 중인 부위는 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
