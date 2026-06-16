package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class ChatRoomAccessDeniedException extends ForbiddenException {

    public ChatRoomAccessDeniedException() {
        super(ChatRoomErrorCode.CHAT_ROOM_ACCESS_DENIED, ChatRoomErrorCode.CHAT_ROOM_ACCESS_DENIED.getMessage());
    }
}
