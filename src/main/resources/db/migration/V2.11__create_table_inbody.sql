CREATE TABLE inbody (
                        inbody_id BIGINT NOT NULL AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        measured_date DATE NOT NULL,
                        height DECIMAL(5,2) NOT NULL,
                        weight DECIMAL(5,2) NOT NULL,
                        body_fat_percentage DECIMAL(5,2) NULL,
                        skeletal_muscle_mass DECIMAL(5,2) NULL,
                        created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        deleted_at DATETIME(6) NULL,
                        CONSTRAINT pk_inbody PRIMARY KEY (inbody_id),
                        CONSTRAINT fk_inbody_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                        INDEX idx_inbody_user_measured_date (user_id, measured_date DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;