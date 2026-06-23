ALTER TABLE workout_diaries
DROP COLUMN deleted_at;

ALTER TABLE workout_diaries
DROP COLUMN status;

ALTER TABLE workout_diaries
    ADD CONSTRAINT uk_workout_diaries_user_date
        UNIQUE (user_id, diary_date);

CREATE INDEX idx_workout_diaries_user_date
    ON workout_diaries (user_id, diary_date);