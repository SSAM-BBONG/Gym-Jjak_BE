package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataChatbotMessageRepository extends JpaRepository<ChatbotMessageJpaEntity, Long> {

    List<ChatbotMessageJpaEntity> findTop12BySessionIdOrderByCreatedAtDesc(String sessionId);

    @Query("""
            SELECT m FROM ChatbotMessageJpaEntity m
            WHERE m.sessionId = :sessionId
              AND (:cursorCreatedAt IS NULL
                   OR m.createdAt < :cursorCreatedAt
                   OR (m.createdAt = :cursorCreatedAt AND m.id < :cursorMessageId))
            ORDER BY m.createdAt DESC, m.id DESC
            """)
    List<ChatbotMessageJpaEntity> findHistory(
            @Param("sessionId") String sessionId,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorMessageId") Long cursorMessageId,
            Pageable pageable
    );
}
