package com.ssambbong.gymjjak.chatbot.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;

/** FastAPI의 내부 도구 호출 컨텍스트가 유효하지 않을 때 사용하는 예외입니다. */
public class ChatbotToolException extends ApplicationException {

    public ChatbotToolException(ChatbotToolErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
