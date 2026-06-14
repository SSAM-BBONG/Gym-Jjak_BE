package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode implements ErrorCode {

    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_001", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ALREADY_EXISTS(HttpStatus.CONFLICT, "CHAT_002", "이미 해당 트레이너와의 채팅방이 존재합니다."),
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT_003", "해당 채팅방에 접근할 권한이 없습니다."),
    CHAT_ROOM_ALREADY_LEFT(HttpStatus.CONFLICT, "CHAT_004", "이미 나간 채팅방입니다."),
    CHAT_ROOM_CLOSED(HttpStatus.BAD_REQUEST, "CHAT_005", "이미 종료된 채팅방입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
