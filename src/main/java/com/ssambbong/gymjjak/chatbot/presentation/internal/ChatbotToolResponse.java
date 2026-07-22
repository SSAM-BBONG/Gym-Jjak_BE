package com.ssambbong.gymjjak.chatbot.presentation.internal;

/** FastAPI가 일관되게 파싱할 수 있도록 내부 도구 결과를 감싸는 응답 형식입니다. */
public record ChatbotToolResponse<T>(T data) {
}
