package com.ssambbong.gymjjak.chatbot.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;
import com.ssambbong.gymjjak.global.domain.common.exception.CommonErrorCode;

public class InvalidChatbotMessageCursorException extends BadRequestException {

    public InvalidChatbotMessageCursorException() {
        super(CommonErrorCode.INVALID_INPUT);
    }
}
