ALTER TABLE workout_diaries
    ADD COLUMN exercise_id BIGINT NULL
        COMMENT 'Exercise master id'
        AFTER part;
