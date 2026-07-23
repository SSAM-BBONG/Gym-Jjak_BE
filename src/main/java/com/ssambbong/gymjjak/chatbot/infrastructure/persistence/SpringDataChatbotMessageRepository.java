package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataChatbotMessageRepository extends JpaRepository<ChatbotMessageJpaEntity, Long> {

    List<ChatbotMessageJpaEntity> findTop12BySessionIdOrderByCreatedAtDesc(String sessionId);
}
