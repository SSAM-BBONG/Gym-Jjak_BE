package com.ssambbong.gymjjak.chatbot.application.port.out;

import com.ssambbong.gymjjak.chatbot.application.result.ChatbotInbodySnapshot;

/**
 * 챗봇이 인바디 도메인에서 필요한 최소 정보만 조회하기 위한 출력 포트입니다.
 */
public interface ChatbotInbodyQueryPort {

    ChatbotInbodySnapshot loadLatest(Long userId);
}
