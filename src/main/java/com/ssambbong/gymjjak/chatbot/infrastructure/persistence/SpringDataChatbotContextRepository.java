package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotContextKind;

public interface SpringDataChatbotContextRepository extends JpaRepository<ChatbotContextJpaEntity, Long> {

    @Query("""
            SELECT context
            FROM ChatbotContextJpaEntity context
            WHERE context.sessionId = :sessionId
              AND context.userId = :userId
              AND (context.expiresAt IS NULL OR context.expiresAt > :now)
            ORDER BY context.createdAt ASC
            """)
    List<ChatbotContextJpaEntity> findActiveBySessionIdAndUserId(
            @Param("sessionId") String sessionId,
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    Optional<ChatbotContextJpaEntity> findBySessionIdAndUserIdAndKind(
            String sessionId, Long userId, ChatbotContextKind kind
    );
}
