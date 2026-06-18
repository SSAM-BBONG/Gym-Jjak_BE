package com.ssambbong.gymjjak.user.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode {

    TEMPORARY_PASSWORD_MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL_001", "임시 비밀번호 메일 발송에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
