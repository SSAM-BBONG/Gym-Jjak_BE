UPDATE trainer_applications
SET organization_id = 1
WHERE organization_id IS NULL;

ALTER TABLE trainer_applications
    MODIFY COLUMN organization_id BIGINT NOT NULL;