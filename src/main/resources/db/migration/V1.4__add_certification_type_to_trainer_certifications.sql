ALTER TABLE trainer_certifications
    ADD COLUMN certification_type VARCHAR(30) NOT NULL DEFAULT 'ADDITIONAL' AFTER file_id;

UPDATE trainer_certifications
SET certification_type = 'REQUIRED'
WHERE file_id IS NOT NULL;