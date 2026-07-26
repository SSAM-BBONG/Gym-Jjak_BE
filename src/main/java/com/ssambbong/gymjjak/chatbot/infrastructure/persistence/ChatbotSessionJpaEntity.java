package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionStatus;
import com.ssambbong.gymjjak.global.infrastructure.presentation.CreatedUpdatedEntity;
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
import java.util.UUID;

@Getter
@Entity
@Table(name = "chatbot_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatbotSessionJpaEntity extends CreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatbot_session_id")
    private Long id;

    // 채팅방 sesstionId
    @Column(name = "session_id", nullable = false, unique = true, length = 36)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 채팅방 제목
    @Column(name = "title", length = 100)
    private String title;

    // 채팅방 내용 요약, 최근 12개 문맥 요약
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    // 세션 상태, ACTIVE
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ChatbotSessionStatus status;

    // 마지막 메시지 시각
    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    // FastAPI 스트리밍 요청의 UUID, 동시 메시지 방지용
    @Column(name = "active_request_id", length = 36)
    private String activeRequestId;

    // 스트림 잠금 만료 시각, 120초 뒤 새 요청 허용
    @Column(name = "active_stream_expires_at")
    private LocalDateTime activeStreamExpiresAt;

    private ChatbotSessionJpaEntity(Long userId, String sessionId, LocalDateTime now) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.status = ChatbotSessionStatus.ACTIVE;
        this.lastActivityAt = now;
    }

    public static ChatbotSessionJpaEntity create(Long userId, LocalDateTime now) {
        return new ChatbotSessionJpaEntity(userId, UUID.randomUUID().toString(), now);
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }

    /**
     * FastAPI 도구 호출이 현재 실행 중인 스트림에서 파생되었는지 확인합니다.
     * 세션 UUID만 아는 요청이나 이미 끝난 요청은 여기서 차단됩니다.
     */
    public boolean isActiveToolRequest(String requestId, LocalDateTime now) {
        return requestId != null
                && requestId.equals(activeRequestId)
                && activeStreamExpiresAt != null
                && activeStreamExpiresAt.isAfter(now);
    }

    public void touch(LocalDateTime now) {
        this.lastActivityAt = now;
    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }
}
