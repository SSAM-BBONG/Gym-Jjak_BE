package com.ssambbong.gymjjak.chatbot;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotPersistenceMappingTest {

    @Test
    void mapsChatbotPersistenceTypesToDedicatedTables() throws ClassNotFoundException {
        assertEntityTable(
                "com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionJpaEntity",
                "chatbot_sessions"
        );
        assertEntityTable(
                "com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotMessageJpaEntity",
                "chatbot_messages"
        );
        assertEntityTable(
                "com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotContextJpaEntity",
                "chatbot_contexts"
        );
    }

    private void assertEntityTable(String className, String tableName) throws ClassNotFoundException {
        Class<?> entityType = Class.forName(className);

        assertThat(entityType.isAnnotationPresent(Entity.class)).isTrue();
        assertThat(entityType.getAnnotation(Table.class).name()).isEqualTo(tableName);
    }
}
