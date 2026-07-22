package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotContextKind;
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

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "chatbot_contexts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatbotContextJpaEntity extends CreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatbot_context_id")
    private Long id;

    // 챗봇 채팅방 1개를 식별하는 UUID, FK
    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 대화 기억 정보의 분류
    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 30)
    private ChatbotContextKind kind;

    // 실제 기억 내용
    @Column(name = "value", nullable = false, length = 500)
    private String value;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public ChatbotContextJpaEntity(
            String sessionId, Long userId, ChatbotContextKind kind, String value,
            LocalDateTime expiresAt
    ) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.kind = kind;
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public boolean isExpiredAt(LocalDateTime now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }
}
