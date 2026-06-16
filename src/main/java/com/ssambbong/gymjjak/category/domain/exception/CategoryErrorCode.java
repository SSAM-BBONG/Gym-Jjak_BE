package com.ssambbong.gymjjak.category.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CategoryErrorCode implements ErrorCode {

    CATEGORY_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "CATEGORY_001", "입력된 이름이 없습니다."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "CATEGORY_002", "이미 존재하는 카테고리입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_003", "카테고리를 찾을 수 없습니다."),
    CATEGORY_IN_USE(HttpStatus.CONFLICT, "CATEGORY_004", "사용 중인 카테고리는 삭제할 수 없습니다.");

    // 필드
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
