package com.ssambbong.gymjjak.chatbot.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;

public class ChatbotSessionException extends ApplicationException {

    public ChatbotSessionException(ChatbotErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
