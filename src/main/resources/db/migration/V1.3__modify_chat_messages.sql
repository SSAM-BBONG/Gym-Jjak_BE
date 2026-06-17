-- chat_messages: is_read 컬럼 추가
ALTER TABLE chat_messages
    ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT FALSE AFTER content;
