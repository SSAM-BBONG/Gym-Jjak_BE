ALTER TABLE notifications
    ADD COLUMN category VARCHAR(50) NULL AFTER notification_type,
    ADD COLUMN content VARCHAR(255) NULL AFTER title,
    ADD COLUMN event_at DATETIME(6) NULL AFTER target_id,
    ADD COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6) AFTER created_at;

UPDATE notifications
SET category = CASE
                   WHEN notification_type LIKE 'PT_%' THEN 'PT'
                   WHEN notification_type LIKE 'FEEDBACK_%' THEN 'FEEDBACK'
                   WHEN notification_type LIKE 'ORGANIZATION_%' THEN 'ORGANIZATION'
                   WHEN notification_type LIKE 'TRAINER_%' THEN 'TRAINER'
                   ELSE 'PT'
    END,
    content = title
WHERE category IS NULL;

ALTER TABLE notifications
    MODIFY COLUMN category VARCHAR(50) NOT NULL,
    MODIFY COLUMN content VARCHAR(255) NOT NULL;

CREATE INDEX idx_notifications_receiver_visible
    ON notifications (receiver_id, deleted_at, expires_at, created_at);