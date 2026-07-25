package com.ssambbong.gymjjak.chatbot.presentation.websocket.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 프론트가 보낸 json 메시지 본문
public record SendChatbotMessageRequest(
        String sessionId,
        @NotBlank @Size(max = 5000) String content,
        // ai 추론에 도움되는 힌트
        @Size(max = 50) String intentHint,
        // 빠른 선택 버튼 : ai가 알아듣는 id, 버튼 이름
        ChatbotQuickReplySelection quickReply
) {
}
