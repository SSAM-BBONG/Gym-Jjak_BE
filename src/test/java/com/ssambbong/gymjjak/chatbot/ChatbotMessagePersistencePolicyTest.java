package com.ssambbong.gymjjak.chatbot;

import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotMessageJpaEntity;
import com.ssambbong.gymjjak.global.infrastructure.presentation.CreatedAtEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotMessagePersistencePolicyTest {

    private static final Path MIGRATION_PATH = Path.of(
            "src", "main", "resources", "db", "migration", "V3.8__create_chatbot_tables.sql"
    );

    @Test
    void storesStructuredRoutineAndSourcesForMessageHistoryAndInheritsCreationTimestamp() throws IOException {
        assertThat(ChatbotMessageJpaEntity.class.getSuperclass()).isEqualTo(CreatedAtEntity.class);
        assertThat(Arrays.stream(ChatbotMessageJpaEntity.class.getDeclaredFields())
                .map(field -> field.getName()))
                .doesNotContain("createdAt")
                .contains("routineJson", "sourcesJson");

        String migration = Files.readString(MIGRATION_PATH);
        assertThat(migration)
                .contains("routine_json JSON NULL")
                .contains("sources_json JSON NULL")
                .contains("created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)");
    }
}
