package com.ssambbong.gymjjak.chatbot.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;
import com.ssambbong.gymjjak.global.domain.common.exception.CommonErrorCode;

public class InvalidChatbotSessionCursorException extends BadRequestException {

    public InvalidChatbotSessionCursorException() {
        super(CommonErrorCode.INVALID_INPUT);
    }
}
