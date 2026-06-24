package com.ssambbong.gymjjak.tag.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum TagErrorCode implements ErrorCode {

    TAG_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "TAG_001", "입력된 이름이 없습니다."),
    TAG_ALREADY_EXISTS(HttpStatus.CONFLICT, "TAG_002", "이미 존재하는 태그입니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "TAG_003", "태그를 찾을 수 없습니다."),
    TAG_IN_USE(HttpStatus.CONFLICT, "TAG_004", "사용 중인 태그는 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
