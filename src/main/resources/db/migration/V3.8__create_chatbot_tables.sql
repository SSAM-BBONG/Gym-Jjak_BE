CREATE TABLE chatbot_sessions (
    chatbot_session_id BIGINT NOT NULL AUTO_INCREMENT,
    session_id CHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NULL,
    summary TEXT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    last_activity_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    active_request_id CHAR(36) NULL,
    active_stream_expires_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_chatbot_sessions PRIMARY KEY (chatbot_session_id),
    CONSTRAINT uk_chatbot_sessions_session_id UNIQUE (session_id),
    CONSTRAINT fk_chatbot_sessions_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_chatbot_sessions_user_activity (user_id, last_activity_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chatbot_messages (
    chatbot_message_id BIGINT NOT NULL AUTO_INCREMENT,
    session_id CHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    intent_hint VARCHAR(50) NULL,
    category VARCHAR(30) NULL,
    routine_json JSON NULL,
    sources_json JSON NULL,
    limited BOOLEAN NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_chatbot_messages PRIMARY KEY (chatbot_message_id),
    CONSTRAINT fk_chatbot_messages_session FOREIGN KEY (session_id)
        REFERENCES chatbot_sessions(session_id) ON DELETE CASCADE,
    CONSTRAINT chk_chatbot_messages_role CHECK (role IN ('USER', 'ASSISTANT')),
    INDEX idx_chatbot_messages_session_created (session_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chatbot_contexts (
    chatbot_context_id BIGINT NOT NULL AUTO_INCREMENT,
    session_id CHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    kind VARCHAR(30) NOT NULL,
    value VARCHAR(500) NOT NULL,
    expires_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_chatbot_contexts PRIMARY KEY (chatbot_context_id),
    CONSTRAINT uk_chatbot_contexts_session_kind UNIQUE (session_id, kind),
    CONSTRAINT fk_chatbot_contexts_session FOREIGN KEY (session_id)
        REFERENCES chatbot_sessions(session_id) ON DELETE CASCADE,
    CONSTRAINT fk_chatbot_contexts_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_chatbot_contexts_kind CHECK (kind IN ('PAIN', 'ROUTINE_PREFERENCE', 'LOCATION_TIME')),
    INDEX idx_chatbot_contexts_expiry (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
