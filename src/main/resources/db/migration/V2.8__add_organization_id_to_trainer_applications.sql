ALTER TABLE trainer_applications
    ADD COLUMN organization_id BIGINT NULL AFTER user_id;

ALTER TABLE trainer_applications
    ADD CONSTRAINT fk_trainer_applications_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations (organization_id);

CREATE INDEX idx_trainer_applications_organization_status_created_id
    ON trainer_applications (organization_id, status, created_at, trainer_application_id);
