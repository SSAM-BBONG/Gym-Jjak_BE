-- org-application PENDING 목록 조회 filesort 제거 (status 필터 + created_at 정렬 커버)
CREATE INDEX idx_org_app_status_created
    ON organization_applications (status, created_at, organization_application_id);

-- chat_messages 메시지 목록 조회 인덱스 (ORDER BY chat_message_id DESC 커버)
CREATE INDEX idx_chat_messages_room_id
    ON chat_messages (chat_room_id, chat_message_id);
