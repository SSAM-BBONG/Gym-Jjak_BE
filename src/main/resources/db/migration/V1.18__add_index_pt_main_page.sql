CREATE INDEX idx_trainer_profiles_status
    ON trainer_profiles (status);

CREATE INDEX idx_organizations_status
    ON organizations (status);

CREATE INDEX idx_pt_courses_status_deleted_id
    ON pt_courses (status, deleted_at, pt_course_id);