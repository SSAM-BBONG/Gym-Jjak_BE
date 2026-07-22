package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;
import com.ssambbong.gymjjak.global.infrastructure.presentation.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chatbot_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatbotMessageJpaEntity extends CreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatbot_message_id")
    private Long id;

    // 채팅방 sessionId, FK
    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    // 작성 추제, 사용자, ai
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ChatbotMessageRole role;

    // 질문 or 최종 답변
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 사용자가 버튼 등으로 전달한 의도 힌트
    @Column(name = "intent_hint", length = 50)
    private String intentHint;

    // ai가 답변 분류한 카테고리
    @Column(name = "category", length = 30)
    private String category;

    @Column(name = "routine_json", columnDefinition = "JSON")
    private String routineJson;

    @Column(name = "sources_json", columnDefinition = "JSON")
    private String sourcesJson;

    // 구독,권한,응답 제한 여부, true = 제한된 답변
    @Column(name = "limited")
    private Boolean limited;

    private ChatbotMessageJpaEntity(
            String sessionId, ChatbotMessageRole role, String content, String intentHint,
            String category, String routineJson, String sourcesJson, Boolean limited
    ) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.intentHint = intentHint;
        this.category = category;
        this.routineJson = routineJson;
        this.sourcesJson = sourcesJson;
        this.limited = limited;
    }

    public static ChatbotMessageJpaEntity user(String sessionId, String content, String intentHint) {
        return new ChatbotMessageJpaEntity(sessionId, ChatbotMessageRole.USER, content, intentHint,
                null, null, null, null);
    }

    public static ChatbotMessageJpaEntity assistant(
            String sessionId, String content, String category, String routineJson,
            String sourcesJson, boolean limited
    ) {
        return new ChatbotMessageJpaEntity(sessionId, ChatbotMessageRole.ASSISTANT, content, null,
                category, routineJson, sourcesJson, limited);
    }

    public static ChatbotMessageJpaEntity assistant(
            String sessionId, String content, String category, boolean limited
    ) {
        return assistant(sessionId, content, category, null, null, limited);
    }
}
