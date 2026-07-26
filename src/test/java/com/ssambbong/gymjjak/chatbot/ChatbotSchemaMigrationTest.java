package com.ssambbong.gymjjak.chatbot;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotSchemaMigrationTest {

    private static final Path MIGRATION_PATH = Path.of(
            "src", "main", "resources", "db", "migration", "V3.8__create_chatbot_tables.sql"
    );

    @Test
    void createsChatbotTablesSeparatedFromExistingChatTables() throws IOException {
        assertThat(MIGRATION_PATH).exists();

        String migration = Files.readString(MIGRATION_PATH);

        assertThat(migration)
                .contains("CREATE TABLE chatbot_sessions")
                .contains("CREATE TABLE chatbot_messages")
                .contains("CREATE TABLE chatbot_contexts")
                .contains("CONSTRAINT fk_chatbot_sessions_user")
                .contains("CONSTRAINT fk_chatbot_messages_session")
                .contains("CONSTRAINT fk_chatbot_contexts_session")
                .contains("INDEX idx_chatbot_sessions_user_activity (user_id, last_activity_at)")
                .contains("INDEX idx_chatbot_messages_session_created (session_id, created_at)")
                .contains("INDEX idx_chatbot_contexts_expiry (expires_at)");
    }
}
