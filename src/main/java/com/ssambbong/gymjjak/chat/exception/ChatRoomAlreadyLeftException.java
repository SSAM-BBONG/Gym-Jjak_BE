package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class ChatRoomAlreadyLeftException extends ConflictException {

    public ChatRoomAlreadyLeftException() {
        super(ChatRoomErrorCode.CHAT_ROOM_ALREADY_LEFT, ChatRoomErrorCode.CHAT_ROOM_ALREADY_LEFT.getMessage());
    }
}
