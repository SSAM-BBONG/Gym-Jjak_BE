package com.ssambbong.gymjjak.chat.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRoomResponseCode implements ResponseCode {
    CHAT_ROOM_CREATED("CHAT_ROOM_001", "채팅방 생성 성공");

    private final String code;
    private final String message;
}
