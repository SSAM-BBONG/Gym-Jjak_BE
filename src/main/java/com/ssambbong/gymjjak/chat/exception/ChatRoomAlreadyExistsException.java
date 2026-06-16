package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class ChatRoomAlreadyExistsException extends ConflictException {

    public ChatRoomAlreadyExistsException() {
        super(ChatRoomErrorCode.CHAT_ROOM_ALREADY_EXISTS, ChatRoomErrorCode.CHAT_ROOM_ALREADY_EXISTS.getMessage());
    }
}
