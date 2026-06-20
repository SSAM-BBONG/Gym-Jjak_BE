ALTER TABLE users
    ADD COLUMN social_provider VARCHAR(20) NULL,
    ADD COLUMN social_id VARCHAR(100) NULL,
    ADD CONSTRAINT uk_users_social UNIQUE (social_provider, social_id);

ALTER TABLE users
    MODIFY password VARCHAR(255) NULL,
    MODIFY nickname VARCHAR(50) NULL,
    MODIFY phone VARCHAR(20) NULL;