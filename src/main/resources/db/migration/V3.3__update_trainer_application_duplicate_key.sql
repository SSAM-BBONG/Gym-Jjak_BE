ALTER TABLE trainer_applications
DROP INDEX uk_trainer_applications_duplicate_blocking_user;

ALTER TABLE trainer_applications
DROP COLUMN duplicate_blocking_user_id;

ALTER TABLE trainer_applications
    ADD COLUMN duplicate_blocking_organization_id BIGINT
        GENERATED ALWAYS AS (
            CASE
                WHEN status IN ('PENDING', 'APPROVED') THEN organization_id
                ELSE NULL
                END
            ) STORED;

ALTER TABLE trainer_applications
    ADD UNIQUE KEY uk_trainer_applications_duplicate_blocking_organization (
    user_id,
    duplicate_blocking_organization_id
    );