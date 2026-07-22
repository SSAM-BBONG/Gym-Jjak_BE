package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringDataChatbotSessionRepository extends JpaRepository<ChatbotSessionJpaEntity, Long> {

    Optional<ChatbotSessionJpaEntity> findBySessionId(String sessionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE chatbot_sessions
            SET active_request_id = :requestId,
                active_stream_expires_at = :expiresAt
            WHERE session_id = :sessionId
              AND user_id = :userId
              AND (active_request_id IS NULL OR active_stream_expires_at < :now)
            """, nativeQuery = true)
    int acquireStreamLock(
            @Param("sessionId") String sessionId,
            @Param("userId") Long userId,
            @Param("requestId") String requestId,
            @Param("expiresAt") LocalDateTime expiresAt,
            @Param("now") LocalDateTime now
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE chatbot_sessions
            SET active_request_id = NULL,
                active_stream_expires_at = NULL
            WHERE session_id = :sessionId
              AND active_request_id = :requestId
            """, nativeQuery = true)
    int releaseStreamLock(@Param("sessionId") String sessionId, @Param("requestId") String requestId);
}
