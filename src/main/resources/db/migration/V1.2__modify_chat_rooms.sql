-- chat_rooms: pt_course_id NOT NULL 변경, 가상 컬럼 및 유니크 인덱스 추가
ALTER TABLE chat_rooms DROP FOREIGN KEY fk_chat_rooms_pt_course;

ALTER TABLE chat_rooms
    MODIFY COLUMN pt_course_id BIGINT NOT NULL,
    ADD COLUMN last_message_at DATETIME(6) NULL AFTER status,
    ADD COLUMN active_pt_course_id BIGINT GENERATED ALWAYS AS (
        IF(status = 'ACTIVE', pt_course_id, NULL)
    ) VIRTUAL AFTER last_message_at,
    ADD UNIQUE INDEX uk_chat_rooms_active (user_id, trainer_profile_id, active_pt_course_id);

ALTER TABLE chat_rooms
    ADD CONSTRAINT fk_chat_rooms_pt_course FOREIGN KEY (pt_course_id) REFERENCES pt_courses (pt_course_id);

