package com.ssambbong.gymjjak.chatbot.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 챗봇 내부 도구 호출 전용 오류 코드입니다. */
@Getter
@RequiredArgsConstructor
public enum ChatbotToolErrorCode implements ErrorCode {
    REQUEST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHATBOT_TOOL_403_1", "챗봇 도구 요청에 접근할 수 없습니다."),
    DATE_RANGE_INVALID(HttpStatus.BAD_REQUEST, "CHATBOT_TOOL_400_1", "운동 일지 조회 기간이 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
