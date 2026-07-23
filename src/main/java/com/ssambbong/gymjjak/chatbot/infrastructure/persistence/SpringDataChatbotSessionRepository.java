package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataChatbotSessionRepository extends JpaRepository<ChatbotSessionJpaEntity, Long> {

    Optional<ChatbotSessionJpaEntity> findBySessionId(String sessionId);

    @Query(value = """
            SELECT s.session_id AS sessionId,
                   (SELECT first_message.content
                    FROM chatbot_messages first_message
                    WHERE first_message.session_id = s.session_id
                      AND first_message.role = 'USER'
                    ORDER BY first_message.created_at ASC, first_message.chatbot_message_id ASC
                    LIMIT 1) AS title,
                   (SELECT last_message.content
                    FROM chatbot_messages last_message
                    WHERE last_message.session_id = s.session_id
                    ORDER BY last_message.created_at DESC, last_message.chatbot_message_id DESC
                    LIMIT 1) AS lastMessage,
                   s.last_activity_at AS lastActivityAt
            FROM chatbot_sessions s
            WHERE s.user_id = :userId
              AND (:cursorLastActivityAt IS NULL
                   OR s.last_activity_at < :cursorLastActivityAt
                   OR (s.last_activity_at = :cursorLastActivityAt AND s.session_id < :cursorSessionId))
            ORDER BY s.last_activity_at DESC, s.session_id DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<ChatbotSessionListRow> findSessionList(
            @Param("userId") Long userId,
            @Param("cursorLastActivityAt") LocalDateTime cursorLastActivityAt,
            @Param("cursorSessionId") String cursorSessionId,
            @Param("limit") int limit
    );

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
