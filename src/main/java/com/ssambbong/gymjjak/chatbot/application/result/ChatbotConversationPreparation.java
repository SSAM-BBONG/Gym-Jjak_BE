package com.ssambbong.gymjjak.chatbot.application.result;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshot;

// 읽기 전용 개인 데이터 스냅샷 조회 전까지, 커밋된 챗봇 요청 상태를 보관한다.
public record ChatbotConversationPreparation(
        String sessionId,
        String requestId,
        String content,
        String intentHint,
        String userRole,
        ChatbotAiRequest.Memory memory
) {
    // 읽기 전용 개인 데이터 스냅샷을 합쳐 FastAPI 전송 객체를 완성한다.
    public ChatbotConversationStart toStart(Long userId, ChatbotUserDataSnapshot personalData) {
        return new ChatbotConversationStart(
                sessionId,
                requestId,
                new ChatbotAiRequest(
                        sessionId,
                        content,
                        intentHint,
                        new ChatbotAiRequest.Actor(userId, userRole),
                        memory,
                        personalData,
                        requestId
                )
        );
    }
}
