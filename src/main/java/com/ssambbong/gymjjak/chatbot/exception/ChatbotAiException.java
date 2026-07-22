package com.ssambbong.gymjjak.chatbot.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;

public class ChatbotAiException extends ApplicationException {

    public ChatbotAiException(ChatbotErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public ChatbotAiException(ChatbotErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
