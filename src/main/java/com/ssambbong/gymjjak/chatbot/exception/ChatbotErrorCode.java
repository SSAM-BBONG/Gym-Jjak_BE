package com.ssambbong.gymjjak.chatbot.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatbotErrorCode implements ErrorCode {
    SUBSCRIPTION_REQUIRED(HttpStatus.FORBIDDEN, "CHATBOT_SUBSCRIPTION_REQUIRED", "활성 챗봇 구독이 필요합니다."),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATBOT_SESSION_NOT_FOUND", "챗봇 세션을 찾을 수 없습니다."),
    SESSION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHATBOT_SESSION_ACCESS_DENIED", "챗봇 세션에 접근할 수 없습니다."),
    STREAM_IN_PROGRESS(HttpStatus.CONFLICT, "CHATBOT_STREAM_IN_PROGRESS", "해당 세션의 응답이 이미 생성 중입니다."),
    FASTAPI_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "CHATBOT_502_1", "챗봇 AI 서버 요청에 실패했습니다."),
    FASTAPI_RESPONSE_INVALID(HttpStatus.BAD_GATEWAY, "CHATBOT_502_2", "챗봇 AI 서버 응답 형식이 올바르지 않습니다."),
    FASTAPI_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "CHATBOT_504_1", "챗봇 AI 서버 응답 시간이 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
