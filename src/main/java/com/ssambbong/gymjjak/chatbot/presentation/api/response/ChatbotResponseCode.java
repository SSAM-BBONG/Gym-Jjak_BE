package com.ssambbong.gymjjak.chatbot.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatbotResponseCode implements ResponseCode {

    CHATBOT_SESSION_LIST_SUCCESS(
            "CHATBOT_SESSION_LIST_SUCCESS",
            "챗봇 세션 목록 조회에 성공했습니다."
    );

    private final String code;
    private final String message;
}
