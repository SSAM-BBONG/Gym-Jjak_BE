-- gymjjak reduced dummy data
-- 일반 유저 10명, 트레이너 5명, 조직 1개, 관리자 2명 기준
-- MySQL Workbench Safe Update Mode 에러 방지를 위해 DELETE 대신 TRUNCATE 사용
-- TRUNCATE는 컬럼을 삭제하지 않고 테이블의 데이터만 초기화합니다.
USE gymjjak_db;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE system_logs;
TRUNCATE TABLE admin_action_logs;
TRUNCATE TABLE blacklists;
TRUNCATE TABLE notifications;
TRUNCATE TABLE reports;
TRUNCATE TABLE report_groups;
TRUNCATE TABLE chat_messages;
TRUNCATE TABLE chat_rooms;
TRUNCATE TABLE post_likes;
TRUNCATE TABLE comments;
TRUNCATE TABLE posts;
TRUNCATE TABLE workout_diaries;
TRUNCATE TABLE calendar_entries;
TRUNCATE TABLE trainer_reviews;
TRUNCATE TABLE feedback_media;
TRUNCATE TABLE feedbacks;
TRUNCATE TABLE pt_reservations;
TRUNCATE TABLE pt_curriculums;
TRUNCATE TABLE pt_course_schedules;
TRUNCATE TABLE pt_courses;
TRUNCATE TABLE organization_trainers;
TRUNCATE TABLE trainer_awards;
TRUNCATE TABLE trainer_certifications;
TRUNCATE TABLE trainer_application_awards;
TRUNCATE TABLE trainer_application_certifications;
TRUNCATE TABLE trainer_profiles;
TRUNCATE TABLE trainer_applications;
TRUNCATE TABLE organizations;
TRUNCATE TABLE organization_applications;
TRUNCATE TABLE onboarding_surveys;
TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE files;
TRUNCATE TABLE tags;
TRUNCATE TABLE categories;
TRUNCATE TABLE regions;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (user_id, username, password, name, nickname, phone, role, status, last_login_at, created_at, updated_at, deleted_at) VALUES
                                                                                                                                            (1, 'user01@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자01', 'user01', '010-1000-0001', 'USER', 'ACTIVE', '2026-05-11 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (2, 'user02@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자02', 'user02', '010-1000-0002', 'USER', 'ACTIVE', '2026-05-12 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (3, 'user03@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자03', 'user03', '010-1000-0003', 'USER', 'ACTIVE', '2026-05-13 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (4, 'user04@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자04', 'user04', '010-1000-0004', 'USER', 'ACTIVE', '2026-05-14 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (5, 'user05@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자05', 'user05', '010-1000-0005', 'USER', 'ACTIVE', '2026-05-15 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (6, 'user06@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자06', 'user06', '010-1000-0006', 'USER', 'ACTIVE', '2026-05-16 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (7, 'user07@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자07', 'user07', '010-1000-0007', 'USER', 'ACTIVE', '2026-05-17 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (8, 'user08@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자08', 'user08', '010-1000-0008', 'USER', 'ACTIVE', '2026-05-18 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (9, 'user09@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자09', 'user09', '010-1000-0009', 'USER', 'ACTIVE', '2026-05-19 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (10, 'user10@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '사용자10', 'user10', '010-1000-0010', 'USER', 'ACTIVE', '2026-05-20 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (11, 'trainer01@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '트레이너01', 'trainer01', '010-2000-0001', 'TRAINER', 'ACTIVE', '2026-05-16 10:00:00.000000', '2026-04-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (12, 'trainer02@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '트레이너02', 'trainer02', '010-2000-0002', 'TRAINER', 'ACTIVE', '2026-05-17 10:00:00.000000', '2026-04-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (13, 'trainer03@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '트레이너03', 'trainer03', '010-2000-0003', 'TRAINER', 'ACTIVE', '2026-05-18 10:00:00.000000', '2026-04-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (14, 'trainer04@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '트레이너04', 'trainer04', '010-2000-0004', 'TRAINER', 'ACTIVE', '2026-05-19 10:00:00.000000', '2026-04-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (15, 'trainer05@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '트레이너05', 'trainer05', '010-2000-0005', 'TRAINER', 'ACTIVE', '2026-05-20 10:00:00.000000', '2026-04-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (16, 'org01@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '짐짝피트니스', 'gymjjak-fit', '010-3000-0001', 'ORGANIZATION', 'ACTIVE', '2026-05-22 10:00:00.000000', '2026-04-03 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (17, 'admin01@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '관리자01', 'admin01', '010-9000-0001', 'ADMIN', 'ACTIVE', '2026-05-27 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                            (18, 'admin02@gymjjak.test', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHIhi7JgGOavOWXnOLaIknfEW9yD7wpe', '관리자02', 'admin02', '010-9000-0002', 'ADMIN', 'ACTIVE', '2026-05-26 10:00:00.000000', '2026-04-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO regions (region_id, sido, sigungu, eupmyeondong, full_name, latitude, longitude) VALUES
                                                                                                 (1, '서울특별시', '강남구', '역삼동', '서울특별시 강남구 역삼동', 37.5007, 127.0365),
                                                                                                 (2, '서울특별시', '송파구', '잠실동', '서울특별시 송파구 잠실동', 37.5133, 127.1002),
                                                                                                 (3, '경기도', '성남시 분당구', '정자동', '경기도 성남시 분당구 정자동', 37.3596, 127.1054),
                                                                                                 (4, '인천광역시', '연수구', '송도동', '인천광역시 연수구 송도동', 37.3839, 126.6433),
                                                                                                 (5, '서울특별시', '마포구', '상암동', '서울특별시 마포구 상암동', 37.5794, 126.889);

INSERT INTO categories (category_id, name, created_at, updated_at, deleted_at) VALUES
                                                                                   (1, '헬스', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                   (2, '필라테스', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                   (3, '요가', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                   (4, '크로스핏', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                   (5, '재활운동', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO tags (tag_id, name, created_at, updated_at, deleted_at) VALUES
                                                                        (1, '다이어트', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                        (2, '근력향상', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                        (3, '체형교정', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                        (4, '초보환영', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                        (5, '바디프로필', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO files (file_id, uploader_id, original_name, stored_name, file_url, content_type, file_size, file_type, status, created_at, deleted_at) VALUES
                                                                                                                                                       (1, 2, 'dummy_file_01.jpg', 'stored_dummy_file_01.jpg', 'https://cdn.gymjjak.test/files/dummy_file_01.jpg', 'image/jpeg', 101024, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (2, 3, 'dummy_file_02.jpg', 'stored_dummy_file_02.jpg', 'https://cdn.gymjjak.test/files/dummy_file_02.jpg', 'image/jpeg', 102048, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (3, 4, 'dummy_file_03.jpg', 'stored_dummy_file_03.jpg', 'https://cdn.gymjjak.test/files/dummy_file_03.jpg', 'image/jpeg', 103072, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (4, 5, 'dummy_file_04.jpg', 'stored_dummy_file_04.jpg', 'https://cdn.gymjjak.test/files/dummy_file_04.jpg', 'image/jpeg', 104096, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (5, 6, 'dummy_file_05.jpg', 'stored_dummy_file_05.jpg', 'https://cdn.gymjjak.test/files/dummy_file_05.jpg', 'image/jpeg', 105120, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (6, 7, 'dummy_file_06.jpg', 'stored_dummy_file_06.jpg', 'https://cdn.gymjjak.test/files/dummy_file_06.jpg', 'image/jpeg', 106144, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (7, 8, 'dummy_file_07.jpg', 'stored_dummy_file_07.jpg', 'https://cdn.gymjjak.test/files/dummy_file_07.jpg', 'image/jpeg', 107168, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (8, 9, 'dummy_file_08.jpg', 'stored_dummy_file_08.jpg', 'https://cdn.gymjjak.test/files/dummy_file_08.jpg', 'image/jpeg', 108192, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (9, 10, 'dummy_file_09.jpg', 'stored_dummy_file_09.jpg', 'https://cdn.gymjjak.test/files/dummy_file_09.jpg', 'image/jpeg', 109216, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (10, 11, 'dummy_file_10.jpg', 'stored_dummy_file_10.jpg', 'https://cdn.gymjjak.test/files/dummy_file_10.jpg', 'image/jpeg', 110240, 'PROFILE_IMAGE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (11, 12, 'dummy_file_11.jpg', 'stored_dummy_file_11.jpg', 'https://cdn.gymjjak.test/files/dummy_file_11.jpg', 'image/jpeg', 111264, 'BUSINESS_LICENSE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (12, 13, 'dummy_file_12.jpg', 'stored_dummy_file_12.jpg', 'https://cdn.gymjjak.test/files/dummy_file_12.jpg', 'image/jpeg', 112288, 'BUSINESS_LICENSE', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (13, 14, 'dummy_file_13.jpg', 'stored_dummy_file_13.jpg', 'https://cdn.gymjjak.test/files/dummy_file_13.jpg', 'image/jpeg', 113312, 'TRAINER_CERTIFICATION', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (14, 15, 'dummy_file_14.jpg', 'stored_dummy_file_14.jpg', 'https://cdn.gymjjak.test/files/dummy_file_14.jpg', 'image/jpeg', 114336, 'TRAINER_CERTIFICATION', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (15, 16, 'dummy_file_15.jpg', 'stored_dummy_file_15.jpg', 'https://cdn.gymjjak.test/files/dummy_file_15.jpg', 'image/jpeg', 115360, 'TRAINER_CERTIFICATION', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (16, 17, 'dummy_file_16.jpg', 'stored_dummy_file_16.jpg', 'https://cdn.gymjjak.test/files/dummy_file_16.jpg', 'image/jpeg', 116384, 'TRAINER_CERTIFICATION', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (17, 18, 'dummy_file_17.jpg', 'stored_dummy_file_17.jpg', 'https://cdn.gymjjak.test/files/dummy_file_17.jpg', 'image/jpeg', 117408, 'TRAINER_CERTIFICATION', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (18, 1, 'dummy_file_18.jpg', 'stored_dummy_file_18.jpg', 'https://cdn.gymjjak.test/files/dummy_file_18.jpg', 'image/jpeg', 118432, 'TRAINER_CERTIFICATION', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (19, 2, 'dummy_file_19.jpg', 'stored_dummy_file_19.jpg', 'https://cdn.gymjjak.test/files/dummy_file_19.jpg', 'image/jpeg', 119456, 'TRAINER_CERTIFICATION', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (20, 3, 'dummy_file_20.jpg', 'stored_dummy_file_20.jpg', 'https://cdn.gymjjak.test/files/dummy_file_20.jpg', 'image/jpeg', 120480, 'TRAINER_CERTIFICATION', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (21, 4, 'dummy_file_21.jpg', 'stored_dummy_file_21.jpg', 'https://cdn.gymjjak.test/files/dummy_file_21.jpg', 'image/jpeg', 121504, 'PT_THUMBNAIL', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (22, 5, 'dummy_file_22.jpg', 'stored_dummy_file_22.jpg', 'https://cdn.gymjjak.test/files/dummy_file_22.jpg', 'image/jpeg', 122528, 'PT_THUMBNAIL', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (23, 6, 'dummy_file_23.jpg', 'stored_dummy_file_23.jpg', 'https://cdn.gymjjak.test/files/dummy_file_23.jpg', 'image/jpeg', 123552, 'PT_THUMBNAIL', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (24, 7, 'dummy_file_24.jpg', 'stored_dummy_file_24.jpg', 'https://cdn.gymjjak.test/files/dummy_file_24.jpg', 'image/jpeg', 124576, 'PT_THUMBNAIL', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (25, 8, 'dummy_file_25.jpg', 'stored_dummy_file_25.jpg', 'https://cdn.gymjjak.test/files/dummy_file_25.jpg', 'image/jpeg', 125600, 'PT_THUMBNAIL', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (26, 9, 'dummy_file_26.jpg', 'stored_dummy_file_26.jpg', 'https://cdn.gymjjak.test/files/dummy_file_26.jpg', 'image/jpeg', 126624, 'FEEDBACK_MEDIA', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (27, 10, 'dummy_file_27.jpg', 'stored_dummy_file_27.jpg', 'https://cdn.gymjjak.test/files/dummy_file_27.jpg', 'image/jpeg', 127648, 'FEEDBACK_MEDIA', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (28, 11, 'dummy_file_28.jpg', 'stored_dummy_file_28.jpg', 'https://cdn.gymjjak.test/files/dummy_file_28.jpg', 'image/jpeg', 128672, 'FEEDBACK_MEDIA', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (29, 12, 'dummy_file_29.jpg', 'stored_dummy_file_29.jpg', 'https://cdn.gymjjak.test/files/dummy_file_29.jpg', 'image/jpeg', 129696, 'FEEDBACK_MEDIA', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (30, 13, 'dummy_file_30.jpg', 'stored_dummy_file_30.jpg', 'https://cdn.gymjjak.test/files/dummy_file_30.jpg', 'image/jpeg', 130720, 'FEEDBACK_MEDIA', 'ACTIVE', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO onboarding_surveys (onboarding_id, user_id, exercise_goal, exercise_period, exercise_frequency, preferred_exercise, preferred_region_id, height, weight, created_at, updated_at) VALUES
                                                                                                                                                                                                 (1, 1, '다이어트', '3개월 미만', '주 3회', '헬스', 1, 166, 61, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (2, 2, '근력 향상', '3개월 미만', '주 4회', '헬스', 2, 167, 62, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (3, 3, '체형 교정', '3개월 미만', '주 5회', '헬스', 3, 168, 63, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (4, 4, '건강 관리', '3개월 미만', '주 2회', '헬스', 4, 169, 64, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (5, 5, '체력 향상', '3개월 미만', '주 3회', '헬스', 5, 170, 65, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (6, 6, '다이어트', '3개월 미만', '주 4회', '헬스', 1, 171, 66, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (7, 7, '근력 향상', '3개월 미만', '주 5회', '헬스', 2, 172, 67, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (8, 8, '체형 교정', '3개월 미만', '주 2회', '헬스', 3, 173, 68, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (9, 9, '건강 관리', '3개월 미만', '주 3회', '헬스', 4, 174, 69, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                 (10, 10, '체력 향상', '3개월 미만', '주 4회', '헬스', 5, 175, 70, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000');

INSERT INTO organization_applications (organization_application_id, applicant_user_id, requested_login_id, business_license_file_id, business_registration_number, business_name, representative_name, representative_phone, opening_date, road_address, jibun_address, detail_address, latitude, longitude, website_url, instagram_url, blog_url, facility_phone, public_data_verified, status, reject_reason, reviewed_by, reviewed_at, created_at, updated_at) VALUES
    (1, 16, 'gymjjakfit', 11, '123-45-67890', '짐짝피트니스 강남점', '김대표', '02-1234-5678', '2024-01-15', '서울특별시 강남구 테헤란로 123', '서울특별시 강남구 역삼동 123-45', '3층', 37.5007, 127.0365, 'https://gymjjak-fit.test', 'https://instagram.com/gymjjak_fit', NULL, '02-1234-5678', 1, 'APPROVED', NULL, 17, '2026-05-01 10:00:00.000000', '2026-04-20 10:00:00.000000', '2026-05-28 10:00:00.000000');

INSERT INTO organizations (organization_id, organization_account_id, owner_user_id, application_id, business_license_file_id, business_registration_number, business_name, representative_name, representative_phone, opening_date, road_address, jibun_address, detail_address, latitude, longitude, website_url, instagram_url, blog_url, facility_phone, status, created_at, updated_at, deleted_at) VALUES
    (1, 16, 16, 1, 11, '123-45-67890', '짐짝피트니스 강남점', '김대표', '02-1234-5678', '2024-01-15', '서울특별시 강남구 테헤란로 123', '서울특별시 강남구 역삼동 123-45', '3층', 37.5007, 127.0365, 'https://gymjjak-fit.test', 'https://instagram.com/gymjjak_fit', NULL, '02-1234-5678', 'ACTIVE', '2026-05-01 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO trainer_applications (trainer_application_id, user_id, profile_file_id, spec, introduction, status, reject_reason, reviewed_by, reviewed_at, created_at, updated_at) VALUES
                                                                                                                                                                                     (1, 11, 1, '4년차 퍼스널 트레이너', '회원 목표에 맞춘 체계적인 PT를 제공합니다.', 'APPROVED', NULL, 17, '2026-05-02 10:00:00.000000', '2026-04-21 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                     (2, 12, 2, '5년차 퍼스널 트레이너', '회원 목표에 맞춘 체계적인 PT를 제공합니다.', 'APPROVED', NULL, 17, '2026-05-02 10:00:00.000000', '2026-04-21 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                     (3, 13, 3, '6년차 퍼스널 트레이너', '회원 목표에 맞춘 체계적인 PT를 제공합니다.', 'APPROVED', NULL, 17, '2026-05-02 10:00:00.000000', '2026-04-21 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                     (4, 14, 4, '7년차 퍼스널 트레이너', '회원 목표에 맞춘 체계적인 PT를 제공합니다.', 'APPROVED', NULL, 17, '2026-05-02 10:00:00.000000', '2026-04-21 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                     (5, 15, 5, '8년차 퍼스널 트레이너', '회원 목표에 맞춘 체계적인 PT를 제공합니다.', 'APPROVED', NULL, 17, '2026-05-02 10:00:00.000000', '2026-04-21 10:00:00.000000', '2026-05-28 10:00:00.000000');

INSERT INTO trainer_profiles (trainer_profile_id, user_id, application_id, profile_file_id, display_name, spec, introduction, average_rating, review_count, status, created_at, updated_at, deleted_at) VALUES
                                                                                                                                                                                                            (1, 11, 1, 1, '트레이너01', '4년차 / 체형교정 전문', '초보자도 안전하게 운동할 수 있도록 지도합니다.', 4.6, 1, 'ACTIVE', '2026-05-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                                                            (2, 12, 2, 2, '트레이너02', '5년차 / 체형교정 전문', '초보자도 안전하게 운동할 수 있도록 지도합니다.', 4.7, 2, 'ACTIVE', '2026-05-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                                                            (3, 13, 3, 3, '트레이너03', '6년차 / 체형교정 전문', '초보자도 안전하게 운동할 수 있도록 지도합니다.', 4.5, 3, 'ACTIVE', '2026-05-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                                                            (4, 14, 4, 4, '트레이너04', '7년차 / 체형교정 전문', '초보자도 안전하게 운동할 수 있도록 지도합니다.', 4.6, 4, 'ACTIVE', '2026-05-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                                                            (5, 15, 5, 5, '트레이너05', '8년차 / 체형교정 전문', '초보자도 안전하게 운동할 수 있도록 지도합니다.', 4.7, 5, 'ACTIVE', '2026-05-02 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO trainer_application_certifications (trainer_application_certification_id, application_id, name, issuer, acquired_date, file_id, created_at) VALUES
                                                                                                                                                            (1, 1, '생활스포츠지도사 2급', '문화체육관광부', '2021-03-01', 14, '2026-05-28 10:00:00.000000'),
                                                                                                                                                            (2, 2, '생활스포츠지도사 2급', '문화체육관광부', '2022-03-01', 15, '2026-05-28 10:00:00.000000'),
                                                                                                                                                            (3, 3, '생활스포츠지도사 2급', '문화체육관광부', '2023-03-01', 16, '2026-05-28 10:00:00.000000'),
                                                                                                                                                            (4, 4, '생활스포츠지도사 2급', '문화체육관광부', '2024-03-01', 17, '2026-05-28 10:00:00.000000'),
                                                                                                                                                            (5, 5, '생활스포츠지도사 2급', '문화체육관광부', '2025-03-01', 18, '2026-05-28 10:00:00.000000');

INSERT INTO trainer_application_awards (trainer_application_award_id, application_id, competition_name, award_name, award_date, description, file_id, created_at) VALUES
                                                                                                                                                                      (1, 1, '전국 피트니스 대회', '입상 1위', '2021-09-10', '더미 수상 경력입니다.', 19, '2026-05-28 10:00:00.000000'),
                                                                                                                                                                      (2, 2, '전국 피트니스 대회', '입상 2위', '2022-09-10', '더미 수상 경력입니다.', 20, '2026-05-28 10:00:00.000000'),
                                                                                                                                                                      (3, 3, '전국 피트니스 대회', '입상 3위', '2023-09-10', '더미 수상 경력입니다.', 21, '2026-05-28 10:00:00.000000'),
                                                                                                                                                                      (4, 4, '전국 피트니스 대회', '입상 4위', '2024-09-10', '더미 수상 경력입니다.', 22, '2026-05-28 10:00:00.000000'),
                                                                                                                                                                      (5, 5, '전국 피트니스 대회', '입상 5위', '2025-09-10', '더미 수상 경력입니다.', 23, '2026-05-28 10:00:00.000000');

INSERT INTO trainer_certifications (trainer_certification_id, trainer_profile_id, name, issuer, acquired_date, file_id, created_at, updated_at, deleted_at) VALUES
                                                                                                                                                                (1, 1, '생활스포츠지도사 2급', '문화체육관광부', '2021-03-01', 14, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                (2, 2, '생활스포츠지도사 2급', '문화체육관광부', '2022-03-01', 15, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                (3, 3, '생활스포츠지도사 2급', '문화체육관광부', '2023-03-01', 16, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                (4, 4, '생활스포츠지도사 2급', '문화체육관광부', '2024-03-01', 17, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                (5, 5, '생활스포츠지도사 2급', '문화체육관광부', '2025-03-01', 18, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO trainer_awards (trainer_award_id, trainer_profile_id, competition_name, award_name, award_date, description, file_id, created_at, updated_at, deleted_at) VALUES
                                                                                                                                                                          (1, 1, '전국 피트니스 대회', '입상 1위', '2021-09-10', '더미 수상 경력입니다.', 19, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                          (2, 2, '전국 피트니스 대회', '입상 2위', '2022-09-10', '더미 수상 경력입니다.', 20, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                          (3, 3, '전국 피트니스 대회', '입상 3위', '2023-09-10', '더미 수상 경력입니다.', 21, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                          (4, 4, '전국 피트니스 대회', '입상 4위', '2024-09-10', '더미 수상 경력입니다.', 22, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                          (5, 5, '전국 피트니스 대회', '입상 5위', '2025-09-10', '더미 수상 경력입니다.', 23, '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO organization_trainers (organization_trainer_id, organization_id, trainer_profile_id, registered_by, registered_at, removed_at) VALUES
                                                                                                                                               (1, 1, 1, 16, '2026-05-03 10:00:00.000000', NULL),
                                                                                                                                               (2, 1, 2, 16, '2026-05-03 10:00:00.000000', NULL),
                                                                                                                                               (3, 1, 3, 16, '2026-05-03 10:00:00.000000', NULL),
                                                                                                                                               (4, 1, 4, 16, '2026-05-03 10:00:00.000000', NULL),
                                                                                                                                               (5, 1, 5, 16, '2026-05-03 10:00:00.000000', NULL);

INSERT INTO pt_courses (pt_course_id, organization_id, trainer_profile_id, category_id, tag_id, thumbnail_file_id, title, description, price, total_session_count, supports_diet_log, supports_workout_log, status, created_at, updated_at, deleted_at) VALUES
                                                                                                                                                                                                                                                            (1, 1, 1, 1, 1, 21, '맞춤 PT 1개월 과정', '트레이너01의 맞춤형 PT 더미 강좌입니다.', 350000, 8, 1, 1, 'VISIBLE', '2026-05-04 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                                                                                                            (2, 1, 2, 2, 2, 22, '맞춤 PT 2개월 과정', '트레이너02의 맞춤형 PT 더미 강좌입니다.', 400000, 8, 1, 1, 'VISIBLE', '2026-05-04 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                                                                                                            (3, 1, 3, 3, 3, 23, '맞춤 PT 3개월 과정', '트레이너03의 맞춤형 PT 더미 강좌입니다.', 450000, 8, 1, 1, 'VISIBLE', '2026-05-04 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                                                                                                            (4, 1, 4, 4, 4, 24, '맞춤 PT 4개월 과정', '트레이너04의 맞춤형 PT 더미 강좌입니다.', 500000, 8, 1, 1, 'VISIBLE', '2026-05-04 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                                                                                                            (5, 1, 5, 5, 5, 25, '맞춤 PT 5개월 과정', '트레이너05의 맞춤형 PT 더미 강좌입니다.', 550000, 8, 1, 1, 'VISIBLE', '2026-05-04 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO pt_course_schedules (pt_course_schedule_id, pt_course_id, day_of_week, start_time, end_time, created_at, updated_at) VALUES
                                                                                                                                     (1, 1, 'MONDAY', '10:00:00', '11:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (2, 1, 'TUESDAY', '19:00:00', '20:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (3, 2, 'TUESDAY', '10:00:00', '11:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (4, 2, 'WEDNESDAY', '19:00:00', '20:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (5, 3, 'WEDNESDAY', '10:00:00', '11:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (6, 3, 'THURSDAY', '19:00:00', '20:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (7, 4, 'THURSDAY', '10:00:00', '11:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (8, 4, 'FRIDAY', '19:00:00', '20:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (9, 5, 'FRIDAY', '10:00:00', '11:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                     (10, 5, 'MONDAY', '19:00:00', '20:00:00', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000');

INSERT INTO pt_curriculums (pt_curriculum_id, pt_course_id, session_no, title, content, created_at, updated_at) VALUES
                                                                                                                    (1, 1, 1, '1회차 커리큘럼', '1회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (2, 1, 2, '2회차 커리큘럼', '2회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (3, 1, 3, '3회차 커리큘럼', '3회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (4, 1, 4, '4회차 커리큘럼', '4회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (5, 1, 5, '5회차 커리큘럼', '5회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (6, 1, 6, '6회차 커리큘럼', '6회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (7, 1, 7, '7회차 커리큘럼', '7회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (8, 1, 8, '8회차 커리큘럼', '8회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (9, 2, 1, '1회차 커리큘럼', '1회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (10, 2, 2, '2회차 커리큘럼', '2회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (11, 2, 3, '3회차 커리큘럼', '3회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (12, 2, 4, '4회차 커리큘럼', '4회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (13, 2, 5, '5회차 커리큘럼', '5회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (14, 2, 6, '6회차 커리큘럼', '6회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (15, 2, 7, '7회차 커리큘럼', '7회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (16, 2, 8, '8회차 커리큘럼', '8회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (17, 3, 1, '1회차 커리큘럼', '1회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (18, 3, 2, '2회차 커리큘럼', '2회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (19, 3, 3, '3회차 커리큘럼', '3회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (20, 3, 4, '4회차 커리큘럼', '4회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (21, 3, 5, '5회차 커리큘럼', '5회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (22, 3, 6, '6회차 커리큘럼', '6회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (23, 3, 7, '7회차 커리큘럼', '7회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (24, 3, 8, '8회차 커리큘럼', '8회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (25, 4, 1, '1회차 커리큘럼', '1회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (26, 4, 2, '2회차 커리큘럼', '2회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (27, 4, 3, '3회차 커리큘럼', '3회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (28, 4, 4, '4회차 커리큘럼', '4회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (29, 4, 5, '5회차 커리큘럼', '5회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (30, 4, 6, '6회차 커리큘럼', '6회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (31, 4, 7, '7회차 커리큘럼', '7회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (32, 4, 8, '8회차 커리큘럼', '8회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (33, 5, 1, '1회차 커리큘럼', '1회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (34, 5, 2, '2회차 커리큘럼', '2회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (35, 5, 3, '3회차 커리큘럼', '3회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (36, 5, 4, '4회차 커리큘럼', '4회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (37, 5, 5, '5회차 커리큘럼', '5회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (38, 5, 6, '6회차 커리큘럼', '6회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (39, 5, 7, '7회차 커리큘럼', '7회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                    (40, 5, 8, '8회차 커리큘럼', '8회차 운동 자세 점검 및 단계별 훈련을 진행합니다.', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000');

INSERT INTO pt_reservations (pt_reservation_id, user_id, pt_course_id, organization_id, trainer_profile_id, reserved_start_at, reserved_end_at, cancelled_at, completed_at, progress_count, total_session_count, status, created_at, updated_at) VALUES
                                                                                                                                                                                                                                                     (1, 1, 1, 1, 1, '2026-05-02 10:00:00.000000', '2026-05-02 11:00:00.000000', NULL, '2026-05-02 11:00:00.000000', 1, 8, 'COMPLETED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (2, 2, 2, 1, 2, '2026-05-03 10:00:00.000000', '2026-05-03 11:00:00.000000', NULL, '2026-05-03 11:00:00.000000', 1, 8, 'COMPLETED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (3, 3, 3, 1, 3, '2026-05-04 10:00:00.000000', '2026-05-04 11:00:00.000000', NULL, '2026-05-04 11:00:00.000000', 1, 8, 'COMPLETED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (4, 4, 4, 1, 4, '2026-05-05 10:00:00.000000', '2026-05-05 11:00:00.000000', NULL, '2026-05-05 11:00:00.000000', 1, 8, 'COMPLETED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (5, 5, 5, 1, 5, '2026-05-06 10:00:00.000000', '2026-05-06 11:00:00.000000', NULL, '2026-05-06 11:00:00.000000', 1, 8, 'COMPLETED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (6, 6, 1, 1, 1, '2026-05-07 10:00:00.000000', '2026-05-07 11:00:00.000000', NULL, NULL, 0, 8, 'RESERVED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (7, 7, 2, 1, 2, '2026-05-08 10:00:00.000000', '2026-05-08 11:00:00.000000', NULL, NULL, 0, 8, 'RESERVED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (8, 8, 3, 1, 3, '2026-05-09 10:00:00.000000', '2026-05-09 11:00:00.000000', NULL, NULL, 0, 8, 'RESERVED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (9, 9, 4, 1, 4, '2026-05-10 10:00:00.000000', '2026-05-10 11:00:00.000000', NULL, NULL, 0, 8, 'RESERVED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                                                                                     (10, 10, 5, 1, 5, '2026-05-11 10:00:00.000000', '2026-05-11 11:00:00.000000', NULL, NULL, 0, 8, 'RESERVED', '2026-05-01 09:00:00.000000', '2026-05-28 10:00:00.000000');

INSERT INTO feedbacks (feedback_id, pt_reservation_id, pt_curriculum_id, trainer_profile_id, user_id, content, status, created_at, updated_at, deleted_at) VALUES
                                                                                                                                                               (1, 1, 1, 1, 1, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (2, 2, 9, 2, 2, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (3, 3, 17, 3, 3, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (4, 4, 25, 4, 4, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (5, 5, 33, 5, 5, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (6, 6, 1, 1, 6, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (7, 7, 9, 2, 7, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (8, 8, 17, 3, 8, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (9, 9, 25, 4, 9, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                               (10, 10, 33, 5, 10, '운동 수행 자세와 다음 회차 보완점을 정리한 피드백입니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO feedback_media (feedback_media_id, feedback_id, file_id, media_type, created_at) VALUES
                                                                                                 (1, 1, 26, 'BEFORE', '2026-05-28 10:00:00.000000'),
                                                                                                 (2, 2, 27, 'AFTER', '2026-05-28 10:00:00.000000'),
                                                                                                 (3, 3, 28, 'BEFORE', '2026-05-28 10:00:00.000000'),
                                                                                                 (4, 4, 29, 'AFTER', '2026-05-28 10:00:00.000000'),
                                                                                                 (5, 5, 30, 'BEFORE', '2026-05-28 10:00:00.000000');

INSERT INTO trainer_reviews (trainer_review_id, user_id, trainer_profile_id, pt_course_id, pt_reservation_id, rating, content, status, created_at, updated_at, deleted_at) VALUES
                                                                                                                                                                               (1, 1, 1, 1, 1, 5, '친절하고 체계적인 수업이었습니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                               (2, 2, 2, 2, 2, 4, '친절하고 체계적인 수업이었습니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                               (3, 3, 3, 3, 3, 5, '친절하고 체계적인 수업이었습니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                               (4, 4, 4, 4, 4, 4, '친절하고 체계적인 수업이었습니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                                               (5, 5, 5, 5, 5, 5, '친절하고 체계적인 수업이었습니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO calendar_entries (calendar_entry_id, user_id, entry_type, target_id, title, entry_date, sticker_type, created_at) VALUES
                                                                                                                                  (1, 1, 'PT_RESERVATION', 1, 'PT 예약 1', '2026-05-02', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (2, 2, 'PT_RESERVATION', 2, 'PT 예약 2', '2026-05-03', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (3, 3, 'PT_RESERVATION', 3, 'PT 예약 3', '2026-05-04', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (4, 4, 'PT_RESERVATION', 4, 'PT 예약 4', '2026-05-05', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (5, 5, 'PT_RESERVATION', 5, 'PT 예약 5', '2026-05-06', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (6, 6, 'PT_RESERVATION', 6, 'PT 예약 6', '2026-05-07', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (7, 7, 'PT_RESERVATION', 7, 'PT 예약 7', '2026-05-08', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (8, 8, 'PT_RESERVATION', 8, 'PT 예약 8', '2026-05-09', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (9, 9, 'PT_RESERVATION', 9, 'PT 예약 9', '2026-05-10', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (10, 10, 'PT_RESERVATION', 10, 'PT 예약 10', '2026-05-11', 'DUMBBELL', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (11, 1, 'WORKOUT_DIARY', 1, '운동일지 1', '2026-05-06', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (12, 2, 'WORKOUT_DIARY', 2, '운동일지 2', '2026-05-07', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (13, 3, 'WORKOUT_DIARY', 3, '운동일지 3', '2026-05-08', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (14, 4, 'WORKOUT_DIARY', 4, '운동일지 4', '2026-05-09', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (15, 5, 'WORKOUT_DIARY', 5, '운동일지 5', '2026-05-10', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (16, 6, 'WORKOUT_DIARY', 6, '운동일지 6', '2026-05-11', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (17, 7, 'WORKOUT_DIARY', 7, '운동일지 7', '2026-05-12', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (18, 8, 'WORKOUT_DIARY', 8, '운동일지 8', '2026-05-13', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (19, 9, 'WORKOUT_DIARY', 9, '운동일지 9', '2026-05-14', 'CHECK', '2026-05-28 10:00:00.000000'),
                                                                                                                                  (20, 10, 'WORKOUT_DIARY', 10, '운동일지 10', '2026-05-15', 'CHECK', '2026-05-28 10:00:00.000000');

INSERT INTO workout_diaries (workout_diary_id, user_id, category_id, feedback_id, title, content, diary_date, status, created_at, updated_at, deleted_at) VALUES
                                                                                                                                                              (1, 1, 1, 1, '운동일지 1', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-06', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (2, 2, 2, 2, '운동일지 2', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-07', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (3, 3, 3, 3, '운동일지 3', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-08', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (4, 4, 4, 4, '운동일지 4', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-09', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (5, 5, 5, 5, '운동일지 5', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-10', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (6, 6, 1, 6, '운동일지 6', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-11', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (7, 7, 2, 7, '운동일지 7', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-12', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (8, 8, 3, 8, '운동일지 8', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-13', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (9, 9, 4, 9, '운동일지 9', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-14', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                              (10, 10, 5, 10, '운동일지 10', '스쿼트, 벤치프레스, 유산소를 진행했습니다.', '2026-05-15', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO posts (post_id, user_id, post_type, title, content, view_count, like_count, comment_count, status, created_at, updated_at, deleted_at) VALUES
                                                                                                                                                       (1, 1, 'NORMAL', '운동 질문 1', '오늘 운동 루틴에 대한 더미 게시글입니다.', 11, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (2, 2, 'NORMAL', '운동 질문 2', '오늘 운동 루틴에 대한 더미 게시글입니다.', 12, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (3, 3, 'NORMAL', '운동 질문 3', '오늘 운동 루틴에 대한 더미 게시글입니다.', 13, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (4, 4, 'NORMAL', '운동 질문 4', '오늘 운동 루틴에 대한 더미 게시글입니다.', 14, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (5, 5, 'NORMAL', '운동 질문 5', '오늘 운동 루틴에 대한 더미 게시글입니다.', 15, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (6, 6, 'NORMAL', '운동 질문 6', '오늘 운동 루틴에 대한 더미 게시글입니다.', 16, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (7, 7, 'NORMAL', '운동 질문 7', '오늘 운동 루틴에 대한 더미 게시글입니다.', 17, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (8, 8, 'NORMAL', '운동 질문 8', '오늘 운동 루틴에 대한 더미 게시글입니다.', 18, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (9, 9, 'NORMAL', '운동 질문 9', '오늘 운동 루틴에 대한 더미 게시글입니다.', 19, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                                       (10, 10, 'NORMAL', '운동 질문 10', '오늘 운동 루틴에 대한 더미 게시글입니다.', 20, 2, 2, 'VISIBLE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO comments (comment_id, post_id, user_id, content, status, created_at, updated_at, deleted_at) VALUES
                                                                                                             (1, 1, 2, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (2, 2, 3, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (3, 3, 4, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (4, 4, 5, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (5, 5, 6, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (6, 6, 7, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (7, 7, 8, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (8, 8, 9, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (9, 9, 10, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (10, 10, 1, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (11, 1, 2, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (12, 2, 3, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (13, 3, 4, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (14, 4, 5, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (15, 5, 6, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (16, 6, 7, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (17, 7, 8, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (18, 8, 9, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (19, 9, 10, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL),
                                                                                                             (20, 10, 1, '좋은 정보 감사합니다.', 'ACTIVE', '2026-05-28 10:00:00.000000', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO post_likes (like_id, post_id, user_id, created_at) VALUES
                                                                   (1, 1, 2, '2026-05-28 10:00:00.000000'),
                                                                   (2, 1, 6, '2026-05-28 10:00:00.000000'),
                                                                   (3, 2, 3, '2026-05-28 10:00:00.000000'),
                                                                   (4, 2, 7, '2026-05-28 10:00:00.000000'),
                                                                   (5, 3, 4, '2026-05-28 10:00:00.000000'),
                                                                   (6, 3, 8, '2026-05-28 10:00:00.000000'),
                                                                   (7, 4, 5, '2026-05-28 10:00:00.000000'),
                                                                   (8, 4, 9, '2026-05-28 10:00:00.000000'),
                                                                   (9, 5, 6, '2026-05-28 10:00:00.000000'),
                                                                   (10, 5, 10, '2026-05-28 10:00:00.000000'),
                                                                   (11, 6, 7, '2026-05-28 10:00:00.000000'),
                                                                   (12, 6, 1, '2026-05-28 10:00:00.000000'),
                                                                   (13, 7, 8, '2026-05-28 10:00:00.000000'),
                                                                   (14, 7, 2, '2026-05-28 10:00:00.000000'),
                                                                   (15, 8, 9, '2026-05-28 10:00:00.000000'),
                                                                   (16, 8, 3, '2026-05-28 10:00:00.000000'),
                                                                   (17, 9, 10, '2026-05-28 10:00:00.000000'),
                                                                   (18, 9, 4, '2026-05-28 10:00:00.000000'),
                                                                   (19, 10, 1, '2026-05-28 10:00:00.000000'),
                                                                   (20, 10, 5, '2026-05-28 10:00:00.000000');

INSERT INTO chat_rooms (chat_room_id, user_id, trainer_profile_id, pt_course_id, user_left, trainer_left, status, created_at, closed_at, updated_at) VALUES
                                                                                                                                                         (1, 1, 1, 1, 0, 0, 'ACTIVE', '2026-05-10 10:00:00.000000', NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                                         (2, 2, 2, 2, 0, 0, 'ACTIVE', '2026-05-10 10:00:00.000000', NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                                         (3, 3, 3, 3, 0, 0, 'ACTIVE', '2026-05-10 10:00:00.000000', NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                                         (4, 4, 4, 4, 0, 0, 'ACTIVE', '2026-05-10 10:00:00.000000', NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                                         (5, 5, 5, 5, 0, 0, 'ACTIVE', '2026-05-10 10:00:00.000000', NULL, '2026-05-28 10:00:00.000000');

INSERT INTO chat_messages (chat_message_id, chat_room_id, sender_id, content, created_at) VALUES
                                                                                              (1, 1, 1, '상담 메시지 1입니다.', '2026-05-11 10:05:00.000000'),
                                                                                              (2, 1, 11, '상담 메시지 2입니다.', '2026-05-11 10:10:00.000000'),
                                                                                              (3, 1, 1, '상담 메시지 3입니다.', '2026-05-11 10:15:00.000000'),
                                                                                              (4, 1, 11, '상담 메시지 4입니다.', '2026-05-11 10:20:00.000000'),
                                                                                              (5, 1, 1, '상담 메시지 5입니다.', '2026-05-11 10:25:00.000000'),
                                                                                              (6, 2, 2, '상담 메시지 1입니다.', '2026-05-12 10:05:00.000000'),
                                                                                              (7, 2, 12, '상담 메시지 2입니다.', '2026-05-12 10:10:00.000000'),
                                                                                              (8, 2, 2, '상담 메시지 3입니다.', '2026-05-12 10:15:00.000000'),
                                                                                              (9, 2, 12, '상담 메시지 4입니다.', '2026-05-12 10:20:00.000000'),
                                                                                              (10, 2, 2, '상담 메시지 5입니다.', '2026-05-12 10:25:00.000000'),
                                                                                              (11, 3, 3, '상담 메시지 1입니다.', '2026-05-13 10:05:00.000000'),
                                                                                              (12, 3, 13, '상담 메시지 2입니다.', '2026-05-13 10:10:00.000000'),
                                                                                              (13, 3, 3, '상담 메시지 3입니다.', '2026-05-13 10:15:00.000000'),
                                                                                              (14, 3, 13, '상담 메시지 4입니다.', '2026-05-13 10:20:00.000000'),
                                                                                              (15, 3, 3, '상담 메시지 5입니다.', '2026-05-13 10:25:00.000000'),
                                                                                              (16, 4, 4, '상담 메시지 1입니다.', '2026-05-14 10:05:00.000000'),
                                                                                              (17, 4, 14, '상담 메시지 2입니다.', '2026-05-14 10:10:00.000000'),
                                                                                              (18, 4, 4, '상담 메시지 3입니다.', '2026-05-14 10:15:00.000000'),
                                                                                              (19, 4, 14, '상담 메시지 4입니다.', '2026-05-14 10:20:00.000000'),
                                                                                              (20, 4, 4, '상담 메시지 5입니다.', '2026-05-14 10:25:00.000000'),
                                                                                              (21, 5, 5, '상담 메시지 1입니다.', '2026-05-15 10:05:00.000000'),
                                                                                              (22, 5, 15, '상담 메시지 2입니다.', '2026-05-15 10:10:00.000000'),
                                                                                              (23, 5, 5, '상담 메시지 3입니다.', '2026-05-15 10:15:00.000000'),
                                                                                              (24, 5, 15, '상담 메시지 4입니다.', '2026-05-15 10:20:00.000000'),
                                                                                              (25, 5, 5, '상담 메시지 5입니다.', '2026-05-15 10:25:00.000000');

INSERT INTO report_groups (
    report_group_id,
    report_number,
    target_type,
    target_id,
    target_owner_id,
    snapshot_title,
    snapshot_content,
    snapshot_file_url,
    total_report_count,
    effective_report_count,
    review_status,
    sanction_status,
    processed_by,
    created_at,
    updated_at,
    deleted_at
) VALUES
      (
          1,
          'RPT-202605-0001',
          'POST',
          1,
          1,
          '신고 대상 제목 1',
          '신고 대상 내용 스냅샷입니다.',
          NULL,
          2,
          2,
          'PENDING',
          'NONE',
          NULL,
          '2026-05-28 10:00:00.000000',
          '2026-05-28 10:00:00.000000',
          NULL
      ),
      (
          2,
          'RPT-202605-0002',
          'POST',
          2,
          2,
          '신고 대상 제목 2',
          '신고 대상 내용 스냅샷입니다.',
          NULL,
          2,
          2,
          'PENDING',
          'NONE',
          NULL,
          '2026-05-28 10:00:00.000000',
          '2026-05-28 10:00:00.000000',
          NULL
      ),
      (
          3,
          'RPT-202605-0003',
          'POST',
          3,
          3,
          '신고 대상 제목 3',
          '신고 대상 내용 스냅샷입니다.',
          NULL,
          2,
          2,
          'PENDING',
          'NONE',
          NULL,
          '2026-05-28 10:00:00.000000',
          '2026-05-28 10:00:00.000000',
          NULL
      ),
      (
          4,
          'RPT-202605-0004',
          'COMMENT',
          4,
          4,
          '신고 대상 제목 4',
          '신고 대상 내용 스냅샷입니다.',
          NULL,
          2,
          2,
          'PENDING',
          'NONE',
          NULL,
          '2026-05-28 10:00:00.000000',
          '2026-05-28 10:00:00.000000',
          NULL
      ),
      (
          5,
          'RPT-202605-0005',
          'COMMENT',
          5,
          5,
          '신고 대상 제목 5',
          '신고 대상 내용 스냅샷입니다.',
          NULL,
          2,
          2,
          'PENDING',
          'NONE',
          NULL,
          '2026-05-28 10:00:00.000000',
          '2026-05-28 10:00:00.000000',
          NULL
      );

INSERT INTO reports (report_id, report_group_id, reporter_id, reason, detail, status, processed_by, processed_at, created_at) VALUES
                                                                                                                                  (1, 1, 4, 'SPAM', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (2, 2, 5, 'ABUSE', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (3, 3, 6, 'INAPPROPRIATE', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (4, 4, 7, 'FALSE_INFO', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (5, 5, 8, 'ETC', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (6, 1, 9, 'SPAM', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (7, 2, 10, 'ABUSE', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (8, 3, 1, 'INAPPROPRIATE', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (9, 4, 2, 'FALSE_INFO', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000'),
                                                                                                                                  (10, 5, 3, 'ETC', '커뮤니티 운영 정책 위반으로 신고합니다.', 'PENDING', NULL, NULL, '2026-05-28 10:00:00.000000');

INSERT INTO notifications (notification_id, receiver_id, notification_type, title, target_type, target_id, read_at, created_at, deleted_at, expires_at) VALUES
                                                                                                                                                            (1, 1, 'PT_RESERVED', '알림 제목 1', 'PT_COURSE', 1, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (2, 2, 'FEEDBACK_CREATED', '알림 제목 2', 'FEEDBACK', 2, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (3, 3, 'REPORT_PROCESSED', '알림 제목 3', 'REPORT_GROUP', 3, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (4, 4, 'TRAINER_APPROVED', '알림 제목 4', 'ORGANIZATION', 4, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (5, 5, 'CHAT_MESSAGE', '알림 제목 5', 'CHAT_ROOM', 5, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (6, 6, 'PT_RESERVED', '알림 제목 6', 'PT_COURSE', 1, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (7, 7, 'FEEDBACK_CREATED', '알림 제목 7', 'FEEDBACK', 2, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (8, 8, 'REPORT_PROCESSED', '알림 제목 8', 'REPORT_GROUP', 3, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (9, 9, 'TRAINER_APPROVED', '알림 제목 9', 'ORGANIZATION', 4, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (10, 10, 'CHAT_MESSAGE', '알림 제목 10', 'CHAT_ROOM', 5, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (11, 11, 'PT_RESERVED', '알림 제목 11', 'PT_COURSE', 1, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (12, 12, 'FEEDBACK_CREATED', '알림 제목 12', 'FEEDBACK', 2, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (13, 13, 'REPORT_PROCESSED', '알림 제목 13', 'REPORT_GROUP', 3, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (14, 14, 'TRAINER_APPROVED', '알림 제목 14', 'ORGANIZATION', 4, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (15, 15, 'CHAT_MESSAGE', '알림 제목 15', 'CHAT_ROOM', 5, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (16, 16, 'PT_RESERVED', '알림 제목 16', 'PT_COURSE', 1, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (17, 17, 'FEEDBACK_CREATED', '알림 제목 17', 'FEEDBACK', 2, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (18, 18, 'REPORT_PROCESSED', '알림 제목 18', 'REPORT_GROUP', 3, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (19, 1, 'TRAINER_APPROVED', '알림 제목 19', 'ORGANIZATION', 4, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000'),
                                                                                                                                                            (20, 2, 'CHAT_MESSAGE', '알림 제목 20', 'CHAT_ROOM', 5, NULL, '2026-05-28 10:00:00.000000', NULL, '2026-06-30 23:59:59.000000');

INSERT INTO blacklists (blacklist_id, user_id, admin_id, type, reason, ended_at, status, source_type, created_at, deleted_at) VALUES
                                                                                                                                  (1, 9, 17, 'SUSPEND_7_DAYS', '반복 신고 누적', '2026-06-28 00:00:00.000000', 'ACTIVE', 'ADMIN', '2026-05-28 10:00:00.000000', NULL),
                                                                                                                                  (2, 10, 18, 'PERMANENT', '운영 정책 위반 경고', NULL, 'ACTIVE', 'SYSTEM', '2026-05-28 10:00:00.000000', NULL);

INSERT INTO admin_action_logs (action_log_id, actor_type, admin_id, action_type, target_type, target_id, before_status, after_status, description, ip_address, trace_id, created_at) VALUES
                                                                                                                                                                                         (1, 'ADMIN', 17, 'USER_SUSPEND', 'USER', 1, NULL, 'RESOLVED', '관리자 액션 로그 1', '192.168.0.1', 'trace-admin-0001', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (2, 'ADMIN', 18, 'REPORT_APPROVE', 'REPORT_GROUP', 2, NULL, 'REJECTED', '관리자 액션 로그 2', '192.168.0.2', 'trace-admin-0002', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (3, 'ADMIN', 17, 'REPORT_REJECT', 'POST', 3, NULL, 'RESOLVED', '관리자 액션 로그 3', '192.168.0.3', 'trace-admin-0003', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (4, 'ADMIN', 18, 'TARGET_BLIND', 'COMMENT', 4, NULL, 'REJECTED', '관리자 액션 로그 4', '192.168.0.4', 'trace-admin-0004', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (5, 'ADMIN', 17, 'BLACKLIST_RELEASE', 'BLACKLIST', 5, NULL, 'RESOLVED', '관리자 액션 로그 5', '192.168.0.5', 'trace-admin-0005', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (6, 'ADMIN', 18, 'USER_SUSPEND', 'USER', 6, NULL, 'REJECTED', '관리자 액션 로그 6', '192.168.0.6', 'trace-admin-0006', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (7, 'ADMIN', 17, 'REPORT_APPROVE', 'REPORT_GROUP', 7, NULL, 'RESOLVED', '관리자 액션 로그 7', '192.168.0.7', 'trace-admin-0007', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (8, 'ADMIN', 18, 'REPORT_REJECT', 'POST', 8, NULL, 'REJECTED', '관리자 액션 로그 8', '192.168.0.8', 'trace-admin-0008', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (9, 'ADMIN', 17, 'TARGET_BLIND', 'COMMENT', 9, NULL, 'RESOLVED', '관리자 액션 로그 9', '192.168.0.9', 'trace-admin-0009', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                                         (10, 'ADMIN', 18, 'BLACKLIST_RELEASE', 'BLACKLIST', 10, NULL, 'REJECTED', '관리자 액션 로그 10', '192.168.0.10', 'trace-admin-0010', '2026-05-28 10:00:00.000000');

INSERT INTO system_logs (system_log_id, trace_id, log_level, request_uri, http_method, remote_addr, user_id, message, exception_class, stack_trace, created_at) VALUES
                                                                                                                                                                    (1, 'trace-000001', 'WARN', '/api/dummy', 'GET', '192.168.0.1', 1, 'WARN 로그 메시지 1', 'java.lang.IllegalArgumentException', 'java.lang.IllegalArgumentException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:1)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (2, 'trace-000002', 'WARN', '/api/dummy', 'POST', '192.168.0.2', 2, 'WARN 로그 메시지 2', 'java.lang.IllegalArgumentException', 'java.lang.IllegalArgumentException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:2)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (3, 'trace-000003', 'ERROR', '/api/dummy', 'GET', '192.168.0.3', 3, 'ERROR 로그 메시지 3', 'java.lang.NullPointerException', 'java.lang.NullPointerException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:3)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (4, 'trace-000004', 'WARN', '/api/dummy', 'POST', '192.168.0.4', 4, 'WARN 로그 메시지 4', 'java.lang.IllegalArgumentException', 'java.lang.IllegalArgumentException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:4)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (5, 'trace-000005', 'WARN', '/api/dummy', 'GET', '192.168.0.5', 5, 'WARN 로그 메시지 5', 'java.lang.IllegalArgumentException', 'java.lang.IllegalArgumentException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:5)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (6, 'trace-000006', 'ERROR', '/api/dummy', 'POST', '192.168.0.6', 6, 'ERROR 로그 메시지 6', 'java.lang.NullPointerException', 'java.lang.NullPointerException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:6)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (7, 'trace-000007', 'WARN', '/api/dummy', 'GET', '192.168.0.7', 7, 'WARN 로그 메시지 7', 'java.lang.IllegalArgumentException', 'java.lang.IllegalArgumentException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:7)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (8, 'trace-000008', 'WARN', '/api/dummy', 'POST', '192.168.0.8', 8, 'WARN 로그 메시지 8', 'java.lang.IllegalArgumentException', 'java.lang.IllegalArgumentException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:8)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (9, 'trace-000009', 'ERROR', '/api/dummy', 'GET', '192.168.0.9', 9, 'ERROR 로그 메시지 9', 'java.lang.NullPointerException', 'java.lang.NullPointerException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:9)', '2026-05-28 10:00:00.000000'),
                                                                                                                                                                    (10, 'trace-000010', 'WARN', '/api/dummy', 'POST', '192.168.0.10', 10, 'WARN 로그 메시지 10', 'java.lang.IllegalArgumentException', 'java.lang.IllegalArgumentException: 더미 예외 스택트레이스
	at com.ssambbong.gymjjak.Dummy.method(Dummy.java:10)', '2026-05-28 10:00:00.000000');

-- End of reduced dummy data