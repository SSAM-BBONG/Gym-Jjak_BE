package com.ssambbong.gymjjak.chat.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageResponseCode implements ResponseCode {
    CHAT_MESSAGE_LIST_FETCHED("CHAT_MESSAGE_001", "채팅 메시지 목록 조회 성공");

    private final String code;
    private final String message;
}
