package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class ChatRoomClosedException extends BadRequestException {

    public ChatRoomClosedException() {
        super(ChatRoomErrorCode.CHAT_ROOM_CLOSED, ChatRoomErrorCode.CHAT_ROOM_CLOSED.getMessage());
    }
}
