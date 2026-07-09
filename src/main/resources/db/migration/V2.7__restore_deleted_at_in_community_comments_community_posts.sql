ALTER TABLE community_posts
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE community_comments
    ADD COLUMN deleted_at DATETIME(6) NULL;