package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class ChatRoomNotFoundException extends NotFoundException {

    public ChatRoomNotFoundException() {
        super(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND, ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
    }
}
