CREATE TABLE trainer_reports (
                                  trainer_report_id BIGINT NOT NULL AUTO_INCREMENT,
                                  trainer_profile_id BIGINT NOT NULL,
                                  target_month DATE NOT NULL,
                                  report TEXT NOT NULL,
                                  market_trends_snapshot JSON NOT NULL,
                                  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                  CONSTRAINT pk_trainer_reports PRIMARY KEY (trainer_report_id),
                                  CONSTRAINT uk_trainer_reports_trainer_month UNIQUE (trainer_profile_id, target_month),
                                  CONSTRAINT fk_trainer_reports_trainer_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
