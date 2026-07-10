ALTER TABLE payments
    DROP FOREIGN KEY fk_payments_pt_reservation,
    DROP COLUMN pt_reservations_id,
    ADD COLUMN pt_course_id BIGINT NULL COMMENT 'PT 결제일 때만 연결' AFTER user_id,
    ADD CONSTRAINT fk_payments_pt_course FOREIGN KEY (pt_course_id) REFERENCES pt_courses (pt_course_id);
