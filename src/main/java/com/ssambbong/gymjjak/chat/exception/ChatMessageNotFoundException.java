package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class ChatMessageNotFoundException extends NotFoundException {

    public ChatMessageNotFoundException() {
        super(ChatRoomErrorCode.CHAT_MESSAGE_NOT_FOUND, ChatRoomErrorCode.CHAT_MESSAGE_NOT_FOUND.getMessage());
    }
}
