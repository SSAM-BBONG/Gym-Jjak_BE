ALTER TABLE organization_trainers ADD INDEX idx_ot_organization_id (organization_id);
ALTER TABLE organization_trainers ADD INDEX idx_ot_trainer_profile_id (trainer_profile_id);

ALTER TABLE organization_trainers DROP INDEX uk_organization_trainers_pair;

ALTER TABLE organization_trainers
    ADD COLUMN active_dedup_key VARCHAR(100)
        GENERATED ALWAYS AS (
            CASE WHEN removed_at IS NULL
                THEN CONCAT(organization_id, '_', trainer_profile_id)
                ELSE NULL
            END
        ) VIRTUAL,
    ADD UNIQUE KEY uk_active_organization_trainer (active_dedup_key);
