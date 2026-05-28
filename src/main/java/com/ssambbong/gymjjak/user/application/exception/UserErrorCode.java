package com.ssambbong.gymjjak.user.application.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "USER_409_001", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER_409_002", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "USER_409_003", "이미 사용 중인 전화번호입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "USER_401_001", "아이디 또는 비밀번호가 올바르지 않습니다."),
    LOGIN_RESTRICTED(HttpStatus.FORBIDDEN, "USER_403_001", "로그인이 제한된 계정입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
