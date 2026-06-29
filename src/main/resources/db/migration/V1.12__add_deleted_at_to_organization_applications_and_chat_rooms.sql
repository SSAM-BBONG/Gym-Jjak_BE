-- organization_applications: 취소/거절된 신청서 소프트딜리트 지원
ALTER TABLE organization_applications
    ADD COLUMN deleted_at DATETIME(6) NULL AFTER updated_at;

CREATE INDEX idx_organization_applications_deleted_at
    ON organization_applications (deleted_at, organization_application_id);

-- chat_rooms: DELETED 상태 채팅방 소프트딜리트 지원
-- chat_messages는 fk_chat_messages_room ON DELETE CASCADE로 채팅방 삭제 시 자동 삭제됨
ALTER TABLE chat_rooms
    ADD COLUMN deleted_at DATETIME(6) NULL AFTER updated_at;

CREATE INDEX idx_chat_rooms_deleted_at
    ON chat_rooms (deleted_at, chat_room_id);
