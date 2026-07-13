ALTER TABLE inbody
    DROP COLUMN deleted_at,
    DROP INDEX idx_inbody_user_measured_date,
    ADD CONSTRAINT uk_inbody_user_measured_date UNIQUE (user_id, measured_date);
