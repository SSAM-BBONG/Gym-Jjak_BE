package com.ssambbong.gymjjak.chat.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRoomResponseCode implements ResponseCode {
    CHAT_ROOM_CREATED("CHAT_ROOM_001", "채팅방 생성 성공"),
    CHAT_ROOM_LEFT("CHAT_ROOM_002", "채팅방 나가기 성공"),
    CHAT_ROOM_LIST_FETCHED("CHAT_ROOM_003", "채팅방 목록 조회 성공"),
    CHAT_UNREAD_COUNT_FETCHED("CHAT_ROOM_004", "안 읽은 메시지 수 조회 성공");

    private final String code;
    private final String message;
}
