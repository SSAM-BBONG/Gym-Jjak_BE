-- GymJjak 1st Project Schema (MySQL 8.x)
-- Version: v4, Team meeting updates applied + single tag per PT course.
-- Generated from ERD export after syntax/constraint cleanup.
-- Notes:
-- 1) Domain enums are stored as VARCHAR columns and validated by Java Enum code.
-- 2) Polymorphic references such as report_groups.target_id and notifications.target_id intentionally do not use FK.
-- 3) system_logs.user_id is an audit snapshot value, intentionally no FK.
-- 4) PT course has exactly one body-part tag, so pt_courses.tag_id directly references tags.tag_id; pt_course_tags join table removed.

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reports;
DROP TABLE IF EXISTS report_groups;
DROP TABLE IF EXISTS admin_action_logs;
DROP TABLE IF EXISTS system_logs;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS blacklists;
DROP TABLE IF EXISTS chat_messages;
DROP TABLE IF EXISTS chat_rooms;
DROP TABLE IF EXISTS post_likes;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS workout_diaries;
DROP TABLE IF EXISTS calendar_entries;
DROP TABLE IF EXISTS trainer_reviews;
DROP TABLE IF EXISTS feedback_media;
DROP TABLE IF EXISTS feedbacks;
DROP TABLE IF EXISTS pt_reservations;
DROP TABLE IF EXISTS pt_course_schedules;
DROP TABLE IF EXISTS pt_curriculums;
DROP TABLE IF EXISTS pt_courses;
DROP TABLE IF EXISTS organization_trainers;
DROP TABLE IF EXISTS trainer_awards;
DROP TABLE IF EXISTS trainer_certifications;
DROP TABLE IF EXISTS trainer_application_awards;
DROP TABLE IF EXISTS trainer_application_certifications;
DROP TABLE IF EXISTS trainer_profiles;
DROP TABLE IF EXISTS trainer_applications;
DROP TABLE IF EXISTS organizations;
DROP TABLE IF EXISTS organization_applications;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS onboarding_surveys;
DROP TABLE IF EXISTS files;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS regions;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
                       user_id BIGINT NOT NULL AUTO_INCREMENT,
                       username VARCHAR(100) NOT NULL COMMENT '로그인 ID. 이메일 형식으로 검증',
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(50) NOT NULL,
                       nickname VARCHAR(50) NOT NULL,
                       phone VARCHAR(20) NOT NULL,
                       role VARCHAR(30) NOT NULL DEFAULT 'USER',
                       status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                       onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
                       last_login_at DATETIME(6) NULL,
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at DATETIME(6) NULL,
                       CONSTRAINT pk_users PRIMARY KEY (user_id),
                       CONSTRAINT uk_users_username UNIQUE (username),
                       CONSTRAINT uk_users_nickname UNIQUE (nickname),
                       CONSTRAINT uk_users_phone UNIQUE (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE regions (
                         region_id BIGINT NOT NULL AUTO_INCREMENT,
                         sido VARCHAR(50) NOT NULL,
                         sigungu VARCHAR(80) NOT NULL,
                         eupmyeondong VARCHAR(80) NULL,
                         full_name VARCHAR(255) NOT NULL,
                         latitude DECIMAL(10,7) NULL,
                         longitude DECIMAL(10,7) NULL,
                         CONSTRAINT pk_regions PRIMARY KEY (region_id),
                         CONSTRAINT uk_regions_area UNIQUE (sido, sigungu, eupmyeondong)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categories (
                            category_id BIGINT NOT NULL AUTO_INCREMENT,
                            name VARCHAR(50) NOT NULL,
                            created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                            updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                            deleted_at DATETIME(6) NULL,
                            CONSTRAINT pk_categories PRIMARY KEY (category_id),
                            CONSTRAINT uk_categories_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tags (
                      tag_id BIGINT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(50) NOT NULL,
                      created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                      updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                      deleted_at DATETIME(6) NULL,
                      CONSTRAINT pk_tags PRIMARY KEY (tag_id),
                      CONSTRAINT uk_tags_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE files (
                       file_id BIGINT NOT NULL AUTO_INCREMENT,
                       uploader_id BIGINT NOT NULL,
                       original_name VARCHAR(255) NOT NULL,
                       stored_name VARCHAR(255) NOT NULL,
                       file_url VARCHAR(500) NOT NULL,
                       content_type VARCHAR(100) NOT NULL,
                       file_size BIGINT NOT NULL,
                       file_type VARCHAR(30) NOT NULL,
                       status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       deleted_at DATETIME(6) NULL,
                       CONSTRAINT pk_files PRIMARY KEY (file_id),
                       CONSTRAINT fk_files_uploader FOREIGN KEY (uploader_id) REFERENCES users(user_id),
                       CONSTRAINT chk_files_size CHECK (file_size >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refresh_tokens (
                                refresh_token_id BIGINT NOT NULL AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                refresh_token VARCHAR(512) NOT NULL,
                                created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                CONSTRAINT pk_refresh_tokens PRIMARY KEY (refresh_token_id),
                                CONSTRAINT uk_refresh_tokens_token UNIQUE (refresh_token),
                                CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE onboarding_surveys (
                                    onboarding_id BIGINT NOT NULL AUTO_INCREMENT,
                                    user_id BIGINT NOT NULL,
                                    exercise_goal VARCHAR(30) NOT NULL,
                                    exercise_period VARCHAR(30) NOT NULL,
                                    exercise_frequency VARCHAR(30) NOT NULL,
                                    preferred_exercise VARCHAR(50) NULL,
                                    preferred_region_id BIGINT NULL,
                                    height DECIMAL(5,2) NULL,
                                    weight DECIMAL(5,2) NULL,
                                    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                    CONSTRAINT pk_onboarding_surveys PRIMARY KEY (onboarding_id),
                                    CONSTRAINT uk_onboarding_surveys_user UNIQUE (user_id),
                                    CONSTRAINT fk_onboarding_surveys_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                    CONSTRAINT fk_onboarding_surveys_region FOREIGN KEY (preferred_region_id) REFERENCES regions(region_id),
                                    CONSTRAINT chk_onboarding_height CHECK (height IS NULL OR height > 0),
                                    CONSTRAINT chk_onboarding_weight CHECK (weight IS NULL OR weight > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE organization_applications (
                                           organization_application_id BIGINT NOT NULL AUTO_INCREMENT,
                                           applicant_user_id BIGINT NOT NULL,
                                           requested_login_id VARCHAR(100) NOT NULL,
                                           business_license_file_id BIGINT NOT NULL,
                                           business_registration_number VARCHAR(30) NOT NULL,
                                           business_name VARCHAR(100) NOT NULL,
                                           representative_name VARCHAR(50) NOT NULL,
                                           representative_phone VARCHAR(20) NOT NULL,
                                           opening_date DATE NOT NULL,
                                           road_address VARCHAR(255) NOT NULL,
                                           jibun_address VARCHAR(255) NULL,
                                           detail_address VARCHAR(255) NULL,
                                           latitude DECIMAL(10,7) NULL,
                                           longitude DECIMAL(10,7) NULL,
                                           website_url VARCHAR(255) NULL,
                                           instagram_url VARCHAR(255) NULL,
                                           blog_url VARCHAR(255) NULL,
                                           facility_phone VARCHAR(20) NULL,
                                           public_data_verified BOOLEAN NOT NULL DEFAULT FALSE,
                                           status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                                           reject_reason VARCHAR(500) NULL,
                                           reviewed_by BIGINT NULL,
                                           reviewed_at DATETIME(6) NULL,
                                           created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                           updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                           CONSTRAINT pk_organization_applications PRIMARY KEY (organization_application_id),
                                           CONSTRAINT uk_org_app_requested_login UNIQUE (requested_login_id),
                                           CONSTRAINT fk_org_app_applicant FOREIGN KEY (applicant_user_id) REFERENCES users(user_id),
                                           CONSTRAINT fk_org_app_license_file FOREIGN KEY (business_license_file_id) REFERENCES files(file_id),
                                           CONSTRAINT fk_org_app_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE organizations (
                               organization_id BIGINT NOT NULL AUTO_INCREMENT,
                               organization_account_id BIGINT NOT NULL,
                               owner_user_id BIGINT NOT NULL,
                               application_id BIGINT NOT NULL,
                               business_license_file_id BIGINT NOT NULL,
                               business_registration_number VARCHAR(30) NOT NULL,
                               business_name VARCHAR(100) NOT NULL,
                               representative_name VARCHAR(50) NOT NULL,
                               representative_phone VARCHAR(20) NOT NULL,
                               opening_date DATE NOT NULL,
                               road_address VARCHAR(255) NOT NULL,
                               jibun_address VARCHAR(255) NULL,
                               detail_address VARCHAR(255) NULL,
                               latitude DECIMAL(10,7) NULL,
                               longitude DECIMAL(10,7) NULL,
                               website_url VARCHAR(255) NULL,
                               instagram_url VARCHAR(255) NULL,
                               blog_url VARCHAR(255) NULL,
                               facility_phone VARCHAR(20) NULL,
                               status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                               created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                               deleted_at DATETIME(6) NULL,
                               CONSTRAINT pk_organizations PRIMARY KEY (organization_id),
                               CONSTRAINT uk_organizations_account UNIQUE (organization_account_id),
                               CONSTRAINT uk_organizations_application UNIQUE (application_id),
                               CONSTRAINT uk_organizations_brn UNIQUE (business_registration_number),
                               CONSTRAINT fk_organizations_account FOREIGN KEY (organization_account_id) REFERENCES users(user_id),
                               CONSTRAINT fk_organizations_owner FOREIGN KEY (owner_user_id) REFERENCES users(user_id),
                               CONSTRAINT fk_organizations_application FOREIGN KEY (application_id) REFERENCES organization_applications(organization_application_id),
                               CONSTRAINT fk_organizations_license_file FOREIGN KEY (business_license_file_id) REFERENCES files(file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trainer_applications (
                                      trainer_application_id BIGINT NOT NULL AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      profile_file_id BIGINT NULL,
                                      spec TEXT NULL,
                                      introduction TEXT NOT NULL,
                                      status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                                      reject_reason VARCHAR(500) NULL,
                                      reviewed_by BIGINT NULL,
                                      reviewed_at DATETIME(6) NULL,
                                      created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                      updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                      CONSTRAINT pk_trainer_applications PRIMARY KEY (trainer_application_id),
                                      CONSTRAINT fk_trainer_applications_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                      CONSTRAINT fk_trainer_applications_profile_file FOREIGN KEY (profile_file_id) REFERENCES files(file_id),
                                      CONSTRAINT fk_trainer_applications_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trainer_profiles (
                                  trainer_profile_id BIGINT NOT NULL AUTO_INCREMENT,
                                  user_id BIGINT NOT NULL,
                                  application_id BIGINT NOT NULL,
                                  profile_file_id BIGINT NULL,
                                  display_name VARCHAR(50) NOT NULL,
                                  spec TEXT NULL,
                                  introduction TEXT NOT NULL,
                                  average_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
                                  review_count INT NOT NULL DEFAULT 0,
                                  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                                  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                  deleted_at DATETIME(6) NULL,
                                  CONSTRAINT pk_trainer_profiles PRIMARY KEY (trainer_profile_id),
                                  CONSTRAINT uk_trainer_profiles_user UNIQUE (user_id),
                                  CONSTRAINT uk_trainer_profiles_application UNIQUE (application_id),
                                  CONSTRAINT fk_trainer_profiles_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                  CONSTRAINT fk_trainer_profiles_application FOREIGN KEY (application_id) REFERENCES trainer_applications(trainer_application_id),
                                  CONSTRAINT fk_trainer_profiles_profile_file FOREIGN KEY (profile_file_id) REFERENCES files(file_id),
                                  CONSTRAINT chk_trainer_profiles_rating CHECK (average_rating >= 0 AND average_rating <= 5),
                                  CONSTRAINT chk_trainer_profiles_review_count CHECK (review_count >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trainer_application_certifications (
                                                    trainer_application_certification_id BIGINT NOT NULL AUTO_INCREMENT,
                                                    application_id BIGINT NOT NULL,
                                                    name VARCHAR(100) NOT NULL,
                                                    issuer VARCHAR(100) NULL,
                                                    acquired_date DATE NULL,
                                                    file_id BIGINT NULL,
                                                    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                                    CONSTRAINT pk_trainer_application_certifications PRIMARY KEY (trainer_application_certification_id),
                                                    CONSTRAINT fk_trainer_app_cert_application FOREIGN KEY (application_id) REFERENCES trainer_applications(trainer_application_id) ON DELETE CASCADE,
                                                    CONSTRAINT fk_trainer_app_cert_file FOREIGN KEY (file_id) REFERENCES files(file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trainer_application_awards (
                                            trainer_application_award_id BIGINT NOT NULL AUTO_INCREMENT,
                                            application_id BIGINT NOT NULL,
                                            competition_name VARCHAR(100) NOT NULL,
                                            award_name VARCHAR(100) NULL,
                                            award_date DATE NULL,
                                            description VARCHAR(255) NULL,
                                            file_id BIGINT NULL,
                                            created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                            CONSTRAINT pk_trainer_application_awards PRIMARY KEY (trainer_application_award_id),
                                            CONSTRAINT fk_trainer_app_award_application FOREIGN KEY (application_id) REFERENCES trainer_applications(trainer_application_id) ON DELETE CASCADE,
                                            CONSTRAINT fk_trainer_app_award_file FOREIGN KEY (file_id) REFERENCES files(file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trainer_certifications (
                                        trainer_certification_id BIGINT NOT NULL AUTO_INCREMENT,
                                        trainer_profile_id BIGINT NOT NULL,
                                        name VARCHAR(100) NOT NULL,
                                        issuer VARCHAR(100) NULL,
                                        acquired_date DATE NULL,
                                        file_id BIGINT NULL,
                                        created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                        updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                        deleted_at DATETIME(6) NULL,
                                        CONSTRAINT pk_trainer_certifications PRIMARY KEY (trainer_certification_id),
                                        CONSTRAINT fk_trainer_certifications_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id),
                                        CONSTRAINT fk_trainer_certifications_file FOREIGN KEY (file_id) REFERENCES files(file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trainer_awards (
                                trainer_award_id BIGINT NOT NULL AUTO_INCREMENT,
                                trainer_profile_id BIGINT NOT NULL,
                                competition_name VARCHAR(100) NOT NULL,
                                award_name VARCHAR(100) NULL,
                                award_date DATE NULL,
                                description VARCHAR(255) NULL,
                                file_id BIGINT NULL,
                                created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                deleted_at DATETIME(6) NULL,
                                CONSTRAINT pk_trainer_awards PRIMARY KEY (trainer_award_id),
                                CONSTRAINT fk_trainer_awards_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id),
                                CONSTRAINT fk_trainer_awards_file FOREIGN KEY (file_id) REFERENCES files(file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE organization_trainers (
                                       organization_trainer_id BIGINT NOT NULL AUTO_INCREMENT,
                                       organization_id BIGINT NOT NULL,
                                       trainer_profile_id BIGINT NOT NULL,
                                       registered_by BIGINT NOT NULL,
                                       registered_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                       removed_at DATETIME(6) NULL,
                                       CONSTRAINT pk_organization_trainers PRIMARY KEY (organization_trainer_id),
                                       CONSTRAINT uk_organization_trainers_pair UNIQUE (organization_id, trainer_profile_id),
                                       CONSTRAINT fk_org_trainers_organization FOREIGN KEY (organization_id) REFERENCES organizations(organization_id),
                                       CONSTRAINT fk_org_trainers_trainer_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id),
                                       CONSTRAINT fk_org_trainers_registered_by FOREIGN KEY (registered_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pt_courses (
                            pt_course_id BIGINT NOT NULL AUTO_INCREMENT,
                            organization_id BIGINT NOT NULL,
                            trainer_profile_id BIGINT NOT NULL,
                            category_id BIGINT NOT NULL,
                            tag_id BIGINT NOT NULL,
                            thumbnail_file_id BIGINT NULL,
                            title VARCHAR(100) NOT NULL,
                            description TEXT NOT NULL,
                            price INT NOT NULL,
                            total_session_count INT NOT NULL,
                            supports_diet_log BOOLEAN NOT NULL DEFAULT FALSE,
                            supports_workout_log BOOLEAN NOT NULL DEFAULT FALSE,
                            status VARCHAR(30) NOT NULL DEFAULT 'VISIBLE',
                            created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                            updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                            deleted_at DATETIME(6) NULL,
                            CONSTRAINT pk_pt_courses PRIMARY KEY (pt_course_id),
                            CONSTRAINT fk_pt_courses_organization FOREIGN KEY (organization_id) REFERENCES organizations(organization_id),
                            CONSTRAINT fk_pt_courses_trainer_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id),
                            CONSTRAINT fk_pt_courses_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
                            CONSTRAINT fk_pt_courses_tag FOREIGN KEY (tag_id) REFERENCES tags(tag_id),
                            CONSTRAINT fk_pt_courses_thumbnail_file FOREIGN KEY (thumbnail_file_id) REFERENCES files(file_id),
                            CONSTRAINT chk_pt_courses_price CHECK (price >= 0),
                            CONSTRAINT chk_pt_courses_total_session CHECK (total_session_count > 0),
                            INDEX idx_pt_courses_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE pt_course_schedules (
                                     pt_course_schedule_id BIGINT NOT NULL AUTO_INCREMENT,
                                     pt_course_id BIGINT NOT NULL,
                                     day_of_week VARCHAR(20) NOT NULL COMMENT 'Java Enum 관리: MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY',
                                     start_time TIME NOT NULL,
                                     end_time TIME NOT NULL,
                                     created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                     updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                     CONSTRAINT pk_pt_course_schedules PRIMARY KEY (pt_course_schedule_id),
                                     CONSTRAINT fk_pt_course_schedules_course FOREIGN KEY (pt_course_id) REFERENCES pt_courses(pt_course_id) ON DELETE CASCADE,
                                     CONSTRAINT chk_pt_course_schedules_time CHECK (end_time > start_time),
                                     CONSTRAINT uk_pt_course_schedules_slot UNIQUE (pt_course_id, day_of_week, start_time, end_time),
                                     INDEX idx_pt_course_schedules_course (pt_course_id),
                                     INDEX idx_pt_course_schedules_day_time (day_of_week, start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE pt_curriculums (
                                pt_curriculum_id BIGINT NOT NULL AUTO_INCREMENT,
                                pt_course_id BIGINT NOT NULL,
                                session_no INT NOT NULL,
                                title VARCHAR(100) NOT NULL,
                                content TEXT NOT NULL,
                                created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                CONSTRAINT pk_pt_curriculums PRIMARY KEY (pt_curriculum_id),
                                CONSTRAINT uk_pt_curriculums_course_session UNIQUE (pt_course_id, session_no),
                                CONSTRAINT fk_pt_curriculums_course FOREIGN KEY (pt_course_id) REFERENCES pt_courses(pt_course_id) ON DELETE CASCADE,
                                CONSTRAINT chk_pt_curriculums_session_no CHECK (session_no > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pt_reservations (
                                 pt_reservation_id BIGINT NOT NULL AUTO_INCREMENT,
                                 user_id BIGINT NOT NULL,
                                 pt_course_id BIGINT NOT NULL,
                                 organization_id BIGINT NOT NULL,
                                 trainer_profile_id BIGINT NOT NULL,
                                 reserved_start_at DATETIME(6) NOT NULL,
                                 reserved_end_at DATETIME(6) NOT NULL,
                                 cancelled_at DATETIME(6) NULL,
                                 completed_at DATETIME(6) NULL,
                                 progress_count INT NOT NULL DEFAULT 0,
                                 total_session_count INT NOT NULL,
                                 status VARCHAR(30) NOT NULL DEFAULT 'RESERVED',
                                 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                 updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                 CONSTRAINT pk_pt_reservations PRIMARY KEY (pt_reservation_id),
                                 CONSTRAINT fk_pt_reservations_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                 CONSTRAINT fk_pt_reservations_course FOREIGN KEY (pt_course_id) REFERENCES pt_courses(pt_course_id),
                                 CONSTRAINT fk_pt_reservations_organization FOREIGN KEY (organization_id) REFERENCES organizations(organization_id),
                                 CONSTRAINT fk_pt_reservations_trainer_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id),
                                 CONSTRAINT chk_pt_reservations_time CHECK (reserved_end_at > reserved_start_at),
                                 CONSTRAINT chk_pt_reservations_progress CHECK (progress_count >= 0 AND progress_count <= total_session_count),
                                 CONSTRAINT chk_pt_reservations_total CHECK (total_session_count > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE feedbacks (
                           feedback_id BIGINT NOT NULL AUTO_INCREMENT,
                           pt_reservation_id BIGINT NOT NULL,
                           pt_curriculum_id BIGINT NOT NULL,
                           trainer_profile_id BIGINT NOT NULL,
                           user_id BIGINT NOT NULL,
                           content TEXT NOT NULL,
                           status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                           created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                           updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                           deleted_at DATETIME(6) NULL,
                           CONSTRAINT pk_feedbacks PRIMARY KEY (feedback_id),
                           CONSTRAINT uk_feedbacks_reservation_curriculum UNIQUE (pt_reservation_id, pt_curriculum_id),
                           CONSTRAINT fk_feedbacks_reservation FOREIGN KEY (pt_reservation_id) REFERENCES pt_reservations(pt_reservation_id),
                           CONSTRAINT fk_feedbacks_curriculum FOREIGN KEY (pt_curriculum_id) REFERENCES pt_curriculums(pt_curriculum_id),
                           CONSTRAINT fk_feedbacks_trainer_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id),
                           CONSTRAINT fk_feedbacks_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE feedback_media (
                                feedback_media_id BIGINT NOT NULL AUTO_INCREMENT,
                                feedback_id BIGINT NOT NULL,
                                file_id BIGINT NOT NULL,
                                media_type VARCHAR(30) NOT NULL,
                                created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                CONSTRAINT pk_feedback_media PRIMARY KEY (feedback_media_id),
                                CONSTRAINT uk_feedback_media_type UNIQUE (feedback_id, media_type),
                                CONSTRAINT fk_feedback_media_feedback FOREIGN KEY (feedback_id) REFERENCES feedbacks(feedback_id) ON DELETE CASCADE,
                                CONSTRAINT fk_feedback_media_file FOREIGN KEY (file_id) REFERENCES files(file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trainer_reviews (
                                 trainer_review_id BIGINT NOT NULL AUTO_INCREMENT,
                                 user_id BIGINT NOT NULL,
                                 trainer_profile_id BIGINT NOT NULL,
                                 pt_course_id BIGINT NOT NULL,
                                 pt_reservation_id BIGINT NOT NULL,
                                 rating TINYINT NOT NULL,
                                 content TEXT NOT NULL,
                                 status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                                 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                 updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                 deleted_at DATETIME(6) NULL,
                                 CONSTRAINT pk_trainer_reviews PRIMARY KEY (trainer_review_id),
                                 CONSTRAINT uk_trainer_reviews_reservation UNIQUE (pt_reservation_id),
                                 CONSTRAINT fk_trainer_reviews_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                 CONSTRAINT fk_trainer_reviews_trainer_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id),
                                 CONSTRAINT fk_trainer_reviews_course FOREIGN KEY (pt_course_id) REFERENCES pt_courses(pt_course_id),
                                 CONSTRAINT fk_trainer_reviews_reservation FOREIGN KEY (pt_reservation_id) REFERENCES pt_reservations(pt_reservation_id),
                                 CONSTRAINT chk_trainer_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE calendar_entries (
                                  calendar_entry_id BIGINT NOT NULL AUTO_INCREMENT,
                                  user_id BIGINT NOT NULL,
                                  entry_type VARCHAR(30) NOT NULL,
                                  target_id BIGINT NULL,
                                  title VARCHAR(100) NOT NULL,
                                  entry_date DATE NOT NULL,
                                  sticker_type VARCHAR(30) NULL,
                                  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                  CONSTRAINT pk_calendar_entries PRIMARY KEY (calendar_entry_id),
                                  CONSTRAINT fk_calendar_entries_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                  INDEX idx_calendar_entries_user_date (user_id, entry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE workout_diaries (
                                 workout_diary_id BIGINT NOT NULL AUTO_INCREMENT,
                                 user_id BIGINT NOT NULL,
                                 category_id BIGINT NOT NULL,
                                 feedback_id BIGINT NULL,
                                 title VARCHAR(100) NOT NULL,
                                 content TEXT NOT NULL,
                                 diary_date DATE NOT NULL,
                                 status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                                 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                 updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                 deleted_at DATETIME(6) NULL,
                                 CONSTRAINT pk_workout_diaries PRIMARY KEY (workout_diary_id),
                                 CONSTRAINT fk_workout_diaries_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                 CONSTRAINT fk_workout_diaries_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
                                 CONSTRAINT fk_workout_diaries_feedback FOREIGN KEY (feedback_id) REFERENCES feedbacks(feedback_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE posts (
                       post_id BIGINT NOT NULL AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       post_type VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
                       title VARCHAR(100) NOT NULL,
                       content TEXT NOT NULL,
                       view_count BIGINT NOT NULL DEFAULT 0,
                       like_count INT NOT NULL DEFAULT 0,
                       comment_count INT NOT NULL DEFAULT 0,
                       status VARCHAR(30) NOT NULL DEFAULT 'VISIBLE',
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at DATETIME(6) NULL,
                       CONSTRAINT pk_posts PRIMARY KEY (post_id),
                       CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                       CONSTRAINT chk_posts_counts CHECK (view_count >= 0 AND like_count >= 0 AND comment_count >= 0),
                       INDEX idx_posts_type_status_created (post_type, status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE comments (
                          comment_id BIGINT NOT NULL AUTO_INCREMENT,
                          post_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                          created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                          updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                          deleted_at DATETIME(6) NULL,
                          CONSTRAINT pk_comments PRIMARY KEY (comment_id),
                          CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
                          CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                          INDEX idx_comments_post_created (post_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE post_likes (
                            like_id BIGINT NOT NULL AUTO_INCREMENT,
                            post_id BIGINT NOT NULL,
                            user_id BIGINT NOT NULL,
                            created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                            CONSTRAINT pk_post_likes PRIMARY KEY (like_id),
                            CONSTRAINT uk_post_likes_post_user UNIQUE (post_id, user_id),
                            CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
                            CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chat_rooms (
                            chat_room_id BIGINT NOT NULL AUTO_INCREMENT,
                            user_id BIGINT NOT NULL,
                            trainer_profile_id BIGINT NOT NULL,
                            pt_course_id BIGINT NULL,
                            user_left BOOLEAN NOT NULL DEFAULT FALSE,
                            trainer_left BOOLEAN NOT NULL DEFAULT FALSE,
                            status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                            created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                            closed_at DATETIME(6) NULL,
                            updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                            CONSTRAINT pk_chat_rooms PRIMARY KEY (chat_room_id),
                            CONSTRAINT fk_chat_rooms_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                            CONSTRAINT fk_chat_rooms_trainer_profile FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(trainer_profile_id),
                            CONSTRAINT fk_chat_rooms_pt_course FOREIGN KEY (pt_course_id) REFERENCES pt_courses(pt_course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chat_messages (
                               chat_message_id BIGINT NOT NULL AUTO_INCREMENT,
                               chat_room_id BIGINT NOT NULL,
                               sender_id BIGINT NOT NULL,
                               content TEXT NOT NULL,
                               created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               CONSTRAINT pk_chat_messages PRIMARY KEY (chat_message_id),
                               CONSTRAINT fk_chat_messages_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE,
                               CONSTRAINT fk_chat_messages_sender FOREIGN KEY (sender_id) REFERENCES users(user_id),
                               INDEX idx_chat_messages_room_created (chat_room_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE report_groups (
                               report_group_id BIGINT NOT NULL AUTO_INCREMENT,
                               report_number VARCHAR(20) NOT NULL,
                               target_type VARCHAR(50) NOT NULL,
                               target_id BIGINT NOT NULL,
                               target_owner_id BIGINT NULL,
                               snapshot_title TEXT NULL,
                               snapshot_content TEXT NULL,
                               snapshot_file_url VARCHAR(500) NULL,

                               total_report_count INT NOT NULL DEFAULT 1,
                               effective_report_count INT NOT NULL DEFAULT 1,

                               review_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                               sanction_status VARCHAR(30) NOT NULL DEFAULT 'NONE',

                               processed_by BIGINT NULL,
                               created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                               deleted_at DATETIME(6) NULL,

                               CONSTRAINT pk_report_groups PRIMARY KEY (report_group_id),
                               CONSTRAINT uk_report_groups_report_number UNIQUE (report_number),
                               CONSTRAINT uk_report_groups_target UNIQUE (target_type, target_id),
                               CONSTRAINT fk_report_groups_target_owner FOREIGN KEY (target_owner_id) REFERENCES users(user_id),
                               CONSTRAINT fk_report_groups_processed_by FOREIGN KEY (processed_by) REFERENCES users(user_id),

                               CONSTRAINT chk_report_groups_total_count CHECK (total_report_count >= 0),
                               CONSTRAINT chk_report_groups_effective_count CHECK (effective_report_count >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reports (
                         report_id BIGINT NOT NULL AUTO_INCREMENT,
                         report_group_id BIGINT NOT NULL,
                         reporter_id BIGINT NOT NULL,
                         reason VARCHAR(50) NOT NULL,
                         detail TEXT NULL,
                         status VARCHAR(30) NOT NULL,
                         processed_by BIGINT NULL,
                         processed_at DATETIME(6) NULL,
                         created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         CONSTRAINT pk_reports PRIMARY KEY (report_id),
                         CONSTRAINT fk_reports_report_group FOREIGN KEY (report_group_id) REFERENCES report_groups(report_group_id),
                         CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users(user_id),
                         CONSTRAINT fk_reports_processed_by FOREIGN KEY (processed_by) REFERENCES users(user_id),
                         INDEX idx_reports_group_created (report_group_id, created_at),
                         INDEX idx_reports_reporter_created (reporter_id, created_at),
                         INDEX idx_reports_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notifications (
                               notification_id BIGINT NOT NULL AUTO_INCREMENT,
                               receiver_id BIGINT NOT NULL,
                               notification_type VARCHAR(50) NOT NULL,
                               title VARCHAR(100) NOT NULL,
                               target_type VARCHAR(50) NULL,
                               target_id BIGINT NULL,
                               read_at DATETIME(6) NULL,
                               created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               deleted_at DATETIME(6) NULL,
                               expires_at DATETIME(6) NULL,
                               CONSTRAINT pk_notifications PRIMARY KEY (notification_id),
                               CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES users(user_id),
                               INDEX idx_notifications_receiver_created (receiver_id, created_at),
                               INDEX idx_notifications_receiver_read (receiver_id, read_at),
                               INDEX idx_notifications_type (notification_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE blacklists (
                            blacklist_id BIGINT NOT NULL AUTO_INCREMENT,
                            user_id BIGINT NOT NULL,
                            admin_id BIGINT NULL,
                            type VARCHAR(30) NOT NULL,
                            reason VARCHAR(500) NOT NULL,
                            ended_at DATETIME(6) NULL,
                            status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                            source_type VARCHAR(20) NOT NULL,
                            created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                            deleted_at DATETIME(6) NULL,
                            CONSTRAINT pk_blacklists PRIMARY KEY (blacklist_id),
                            CONSTRAINT fk_blacklists_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                            CONSTRAINT fk_blacklists_admin FOREIGN KEY (admin_id) REFERENCES users(user_id),
                            INDEX idx_blacklists_user_status (user_id, status),
                            INDEX idx_blacklists_status_created (status, created_at),
                            INDEX idx_blacklists_source_type (source_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE admin_action_logs (
                                   action_log_id BIGINT NOT NULL AUTO_INCREMENT,
                                   actor_type VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
                                   admin_id BIGINT NULL,
                                   action_type VARCHAR(50) NOT NULL,
                                   target_type VARCHAR(50) NOT NULL,
                                   target_id BIGINT NOT NULL,
                                   before_status VARCHAR(50) NULL,
                                   after_status VARCHAR(50) NULL,
                                   description VARCHAR(500) NOT NULL,
                                   ip_address VARCHAR(45) NULL,
                                   trace_id VARCHAR(50) NULL,
                                   created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                   CONSTRAINT pk_admin_action_logs PRIMARY KEY (action_log_id),
                                   CONSTRAINT fk_admin_action_logs_admin FOREIGN KEY (admin_id) REFERENCES users(user_id),
                                   INDEX idx_admin_action_logs_admin_created (admin_id, created_at),
                                   INDEX idx_admin_action_logs_target (target_type, target_id),
                                   INDEX idx_admin_action_logs_trace (trace_id),
                                   INDEX idx_admin_action_logs_action_created (action_type, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE system_logs (
                             system_log_id BIGINT NOT NULL AUTO_INCREMENT,
                             trace_id VARCHAR(50) NOT NULL,
                             log_level VARCHAR(20) NOT NULL,
                             request_uri VARCHAR(255) NULL,
                             http_method VARCHAR(10) NULL,
                             remote_addr VARCHAR(45) NULL,
                             user_id BIGINT NULL COMMENT '로그 추적용 사용자 ID. FK 제약 없음',
                             message VARCHAR(1000) NOT NULL,
                             exception_class VARCHAR(255) NULL,
                             stack_trace LONGTEXT NULL,
                             created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                             CONSTRAINT pk_system_logs PRIMARY KEY (system_log_id),
                             INDEX idx_system_logs_trace_id (trace_id),
                             INDEX idx_system_logs_level_created (log_level, created_at),
                             INDEX idx_system_logs_created_at (created_at),
                             INDEX idx_system_logs_exception_class (exception_class)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
