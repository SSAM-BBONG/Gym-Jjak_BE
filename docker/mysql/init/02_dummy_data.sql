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

-- ---------------------------------------------------------
-- 1. 기초 메타 데이터 (지역, 카테고리, 태그)
-- ---------------------------------------------------------
INSERT INTO regions (sido, sigungu, eupmyeondong, full_name, latitude, longitude)
VALUES ('경기도', '성남시 수정구', '양지동', '경기도 성남시 수정구 양지동', 37.4609960, 127.1651358);

INSERT INTO categories (name) VALUES ('다이어트'), ('근력 향상'), ('체형 교정'), ('재활');
INSERT INTO tags (name) VALUES ('오운완'), ('바디프로필'), ('비포애프터'), ('식단관리');

-- ---------------------------------------------------------
-- 2. 유저 (Users)
-- ID 19~21: 헬스장 원장 (USER) / ID 22~24: 조직 전용 계정 (ORGANIZATION)
-- 비밀번호는 모두 공통 BCrypt 해시 적용
-- ---------------------------------------------------------
INSERT INTO users (username, password, name, nickname, phone, role, status, onboarding_completed) VALUES
                                                                                                      ('user01@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저1', '닉네임1', '010-0000-0001', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user02@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저2', '닉네임2', '010-0000-0002', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user03@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저3', '닉네임3', '010-0000-0003', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user04@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저4', '닉네임4', '010-0000-0004', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user05@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저5', '닉네임5', '010-0000-0005', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user06@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저6', '닉네임6', '010-0000-0006', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('user07@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저7', '닉네임7', '010-0000-0007', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('user08@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저8', '닉네임8', '010-0000-0008', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('user09@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저9', '닉네임9', '010-0000-0009', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('user10@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '유저10', '닉네임10', '010-0000-0010', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('trainer01@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '트레이너1', '득근맨', '010-1111-0001', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer02@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '트레이너2', '헬창인생', '010-1111-0002', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer03@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '트레이너3', '바프장인', '010-1111-0003', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer04@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '트레이너4', '재활마스터', '010-1111-0004', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer05@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '트레이너5', '다이어터', '010-1111-0005', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer06@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '트레이너6', '운동은밥', '010-1111-0006', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('admin01@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '관리자1', '어드민1', '010-9999-0001', 'ADMIN', 'ACTIVE', TRUE),
                                                                                                      ('admin02@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '관리자2', '어드민2', '010-9999-0002', 'ADMIN', 'ACTIVE', TRUE),
                                                                                                      ('owner01@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '원장1', '짐잭대표1', '010-2222-0001', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('owner02@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '원장2', '짐잭대표2', '010-2222-0002', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('owner03@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '원장3', '짐잭대표3', '010-2222-0003', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('org01@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '조직계정1', '조직계정1', '010-3333-0001', 'ORGANIZATION', 'ACTIVE', TRUE),
                                                                                                      ('org02@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '조직계정2', '조직계정2', '010-3333-0002', 'ORGANIZATION', 'ACTIVE', TRUE),
                                                                                                      ('org03@test.com', '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6', '조직계정3', '조직계정3', '010-3333-0003', 'ORGANIZATION', 'ACTIVE', TRUE);

INSERT INTO refresh_tokens (user_id, refresh_token) VALUES
                                                        (1, 'dummy_refresh_token_for_user01'), (11, 'dummy_refresh_token_for_trainer01');

-- ---------------------------------------------------------
-- 3. 온보딩 설문 (Onboarding Surveys)
-- ---------------------------------------------------------
INSERT INTO onboarding_surveys (user_id, exercise_goal, exercise_period, exercise_frequency, preferred_exercise, preferred_region_id, height, weight) VALUES
                                                                                                                                                          (1, '다이어트', '3_MONTHS', 'WEEKLY_3', '웨이트 트레이닝', 1, 175.5, 75.0),
                                                                                                                                                          (2, '근력증가', '6_MONTHS', 'WEEKLY_5', '크로스핏', 1, 180.0, 80.0),
                                                                                                                                                          (3, '체력증진', '1_MONTH', 'WEEKLY_2', '필라테스', 1, 160.0, 50.0),
                                                                                                                                                          (4, '재활', '1_YEAR', 'WEEKLY_1', '요가', 1, NULL, NULL),
                                                                                                                                                          (5, '바디프로필', '6_MONTHS', 'EVERYDAY', '웨이트 트레이닝', 1, 170.0, 65.0);

-- ---------------------------------------------------------
-- 4. 파일(Files)
-- ID 1~3: 승인된 사업자 등록증 / ID 17~19: 승인 대기중인 사업자 등록증
-- ---------------------------------------------------------
INSERT INTO files (uploader_id, original_name, stored_name, file_url, content_type, file_size, file_type, status) VALUES
                                                                                                                      (19, 'license1.pdf', 'uuid-lic1.pdf', 'https://s3.gymjjak.com/uuid-lic1.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (20, 'license2.pdf', 'uuid-lic2.pdf', 'https://s3.gymjjak.com/uuid-lic2.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (21, 'license3.pdf', 'uuid-lic3.pdf', 'https://s3.gymjjak.com/uuid-lic3.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (11, 'profile1.jpg', 'uuid-prof1.jpg', 'https://s3.gymjjak.com/uuid-prof1.jpg', 'image/jpeg', 2048, 'PROFILE_IMAGE', 'ACTIVE'),
                                                                                                                      (12, 'profile2.jpg', 'uuid-prof2.jpg', 'https://s3.gymjjak.com/uuid-prof2.jpg', 'image/jpeg', 2048, 'PROFILE_IMAGE', 'ACTIVE'),
                                                                                                                      (13, 'profile3.jpg', 'uuid-prof3.jpg', 'https://s3.gymjjak.com/uuid-prof3.jpg', 'image/jpeg', 2048, 'PROFILE_IMAGE', 'ACTIVE'),
                                                                                                                      (14, 'profile4.jpg', 'uuid-prof4.jpg', 'https://s3.gymjjak.com/uuid-prof4.jpg', 'image/jpeg', 2048, 'PROFILE_IMAGE', 'ACTIVE'),
                                                                                                                      (15, 'profile5.jpg', 'uuid-prof5.jpg', 'https://s3.gymjjak.com/uuid-prof5.jpg', 'image/jpeg', 2048, 'PROFILE_IMAGE', 'ACTIVE'),
                                                                                                                      (16, 'profile6.jpg', 'uuid-prof6.jpg', 'https://s3.gymjjak.com/uuid-prof6.jpg', 'image/jpeg', 2048, 'PROFILE_IMAGE', 'ACTIVE'),
                                                                                                                      (11, 'thumb1.jpg', 'uuid-thumb1.jpg', 'https://s3.gymjjak.com/uuid-thumb1.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (12, 'thumb2.jpg', 'uuid-thumb2.jpg', 'https://s3.gymjjak.com/uuid-thumb2.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (13, 'thumb3.jpg', 'uuid-thumb3.jpg', 'https://s3.gymjjak.com/uuid-thumb3.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (14, 'thumb4.jpg', 'uuid-thumb4.jpg', 'https://s3.gymjjak.com/uuid-thumb4.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (15, 'thumb5.jpg', 'uuid-thumb5.jpg', 'https://s3.gymjjak.com/uuid-thumb5.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (16, 'thumb6.jpg', 'uuid-thumb6.jpg', 'https://s3.gymjjak.com/uuid-thumb6.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (1, 'feedback_vid1.mp4', 'uuid-fb1.mp4', 'https://s3.gymjjak.com/uuid-fb1.mp4', 'video/mp4', 15000, 'FEEDBACK_VIDEO', 'ACTIVE'),
                                                                                                                      (8, 'license_pending1.pdf', 'uuid-lic-p1.pdf', 'https://s3.gymjjak.com/uuid-lic-p1.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (9, 'license_pending2.pdf', 'uuid-lic-p2.pdf', 'https://s3.gymjjak.com/uuid-lic-p2.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (10, 'license_pending3.pdf', 'uuid-lic-p3.pdf', 'https://s3.gymjjak.com/uuid-lic-p3.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (11, 'trainer_cert1.jpg', 'uuid-tr-cert1.jpg', 'https://s3.gymjjak.com/uuid-tr-cert1.jpg', 'image/jpeg', 2048, 'CERTIFICATION', 'ACTIVE'),
                                                                                                                      (12, 'trainer_cert2.jpg', 'uuid-tr-cert2.jpg', 'https://s3.gymjjak.com/uuid-tr-cert2.jpg', 'image/jpeg', 2048, 'CERTIFICATION', 'ACTIVE'),
                                                                                                                      (13, 'trainer_cert3.jpg', 'uuid-tr-cert3.jpg', 'https://s3.gymjjak.com/uuid-tr-cert3.jpg', 'image/jpeg', 2048, 'CERTIFICATION', 'ACTIVE'),
                                                                                                                      (14, 'trainer_cert4.jpg', 'uuid-tr-cert4.jpg', 'https://s3.gymjjak.com/uuid-tr-cert4.jpg', 'image/jpeg', 2048, 'CERTIFICATION', 'ACTIVE'),
                                                                                                                      (15, 'trainer_cert5.jpg', 'uuid-tr-cert5.jpg', 'https://s3.gymjjak.com/uuid-tr-cert5.jpg', 'image/jpeg', 2048, 'CERTIFICATION', 'ACTIVE'),
                                                                                                                      (16, 'trainer_cert6.jpg', 'uuid-tr-cert6.jpg', 'https://s3.gymjjak.com/uuid-tr-cert6.jpg', 'image/jpeg', 2048, 'CERTIFICATION', 'ACTIVE');

-- ---------------------------------------------------------
-- 5. 조직 (Organization Applications & Organizations)
-- 승인된 내역(ACCEPTED) 3건 + 대기중 내역(PENDING) 3건 추가
-- ---------------------------------------------------------
INSERT INTO organization_applications (applicant_user_id, requested_login_id, business_license_file_id, business_registration_number, business_name, representative_name, representative_phone, opening_date, road_address, latitude, longitude, status) VALUES
                                                                                                                                                                                                                                                             (19, 'org01@test.com', 1, '111-11-11111', '짐잭피트니스 본점', '원장1', '010-2222-0001', '2020-01-01', '경기 성남시 수정구 양지동 212', 37.4609960, 127.1651358, 'ACCEPTED'),
                                                                                                                                                                                                                                                             (20, 'org02@test.com', 2, '222-22-22222', '짐잭피트니스 남한산성점', '원장2', '010-2222-0002', '2021-05-05', '경기 성남시 수정구 양지동 213', 37.4615000, 127.1660000, 'ACCEPTED'),
                                                                                                                                                                                                                                                             (21, 'org03@test.com', 3, '333-33-33333', '짐잭피트니스 단대오거리점', '원장3', '010-2222-0003', '2022-10-10', '경기 성남시 수정구 양지동 214', 37.4590000, 127.1645000, 'ACCEPTED'),
                                                                                                                                                                                                                                                             (8, 'pending_org01@test.com', 17, '444-44-44444', '짐잭피트니스 위례점', '유저8', '010-4444-0001', '2023-01-01', '경기 성남시 수정구 위례광장로 100', 37.4715000, 127.1460000, 'PENDING'),
                                                                                                                                                                                                                                                             (9, 'pending_org02@test.com', 18, '555-55-55555', '짐잭피트니스 판교점', '유저9', '010-5555-0001', '2023-02-01', '경기 성남시 분당구 판교역로 101', 37.3915000, 127.1160000, 'PENDING'),
                                                                                                                                                                                                                                                             (10, 'pending_org03@test.com', 19, '666-66-66666', '짐잭피트니스 서현점', '유저10', '010-6666-0001', '2023-03-01', '경기 성남시 분당구 서현로 102', 37.3815000, 127.1260000, 'PENDING');

INSERT INTO organizations (organization_account_id, owner_user_id, application_id, business_license_file_id, business_registration_number, business_name, representative_name, representative_phone, opening_date, road_address, latitude, longitude, status) VALUES
                                                                                                                                                                                                                                                                  (22, 19, 1, 1, '111-11-11111', '짐잭피트니스 본점', '원장1', '010-2222-0001', '2020-01-01', '경기 성남시 수정구 양지동 212', 37.4609960, 127.1651358, 'ACTIVE'),
                                                                                                                                                                                                                                                                  (23, 20, 2, 2, '222-22-22222', '짐잭피트니스 남한산성점', '원장2', '010-2222-0002', '2021-05-05', '경기 성남시 수정구 양지동 213', 37.4615000, 127.1660000, 'ACTIVE'),
                                                                                                                                                                                                                                                                  (24, 21, 3, 3, '333-33-33333', '짐잭피트니스 단대오거리점', '원장3', '010-2222-0003', '2022-10-10', '경기 성남시 수정구 양지동 214', 37.4590000, 127.1645000, 'ACTIVE');

-- ---------------------------------------------------------
-- 6. 트레이너 프로필 및 자격증/수상 내역
-- ---------------------------------------------------------
INSERT INTO trainer_applications (
    user_id,
    profile_file_id,
    certificate_file_id,
    qualifications,
    award_histories,
    introduction,
    status
) VALUES
      (11, 4, 20, '["생활체육지도자 2급"]', '[]', '안녕하세요. 득근맨입니다.', 'APPROVED'),
      (12, 5, 21, '["건강운동관리사"]', '[]', '정확한 자세를 알려드립니다.', 'APPROVED'),
      (13, 6, 22, '["NASM-CPT"]', '["2023 WNGP 스포츠모델 1위"]', '바프 전문 트레이너입니다.', 'APPROVED'),
      (14, 7, 23, '["재활치료사 면허"]', '[]', '통증 없는 운동을 지향합니다.', 'APPROVED'),
      (15, 8, 24, '["생활체육지도자 1급"]', '[]', '다이어트 확실하게 시켜드립니다.', 'APPROVED'),
      (16, 9, 25, '["크로스핏 레벨1"]', '[]', '체력 증진 전문입니다.', 'APPROVED');

INSERT INTO trainer_profiles (
    user_id,
    application_id,
    profile_file_id,
    trainer_name,
    introduction,
    average_rating,
    review_count,
    status
) VALUES
      (11, 1, 4, '득근맨', '안녕하세요. 득근맨입니다.', 4.5, 10, 'ACTIVE'),
      (12, 2, 5, '헬창인생', '정확한 자세를 알려드립니다.', 4.8, 25, 'ACTIVE'),
      (13, 3, 6, '바프장인', '바프 전문 트레이너입니다.', 5.0, 50, 'ACTIVE'),
      (14, 4, 7, '재활마스터', '통증 없는 운동을 지향합니다.', 4.9, 30, 'ACTIVE'),
      (15, 5, 8, '다이어터', '다이어트 확실하게 시켜드립니다.', 4.2, 5, 'ACTIVE'),
      (16, 6, 9, '운동은밥', '체력 증진 전문입니다.', 4.7, 15, 'ACTIVE');

INSERT INTO trainer_certifications (
    trainer_profile_id,
    name,
    file_id
) VALUES
      (1, '생활체육지도자 2급', 20),
      (2, '건강운동관리사', 21),
      (3, 'NASM-CPT', 22),
      (4, '재활치료사 면허', 23),
      (5, '생활체육지도자 1급', 24),
      (6, '크로스핏 레벨1', 25);

INSERT INTO trainer_awards (
    trainer_profile_id,
    name
) VALUES
    (3, '2023 WNGP 스포츠모델 1위');

INSERT INTO organization_trainers (organization_id, trainer_profile_id, registered_by) VALUES
                                                                                           (1, 1, 19), (1, 2, 19), (2, 3, 20), (2, 4, 20), (3, 5, 21), (3, 6, 21);

-- ---------------------------------------------------------
-- 7. PT 코스 및 스케줄, 커리큘럼
-- ---------------------------------------------------------
INSERT INTO pt_courses (organization_id, trainer_profile_id, category_id, tag_id, thumbnail_file_id, title, description, price, total_session_count, supports_diet_log, supports_workout_log, status) VALUES
                                                                                                                                                                                                          (1, 1, 2, 1, 10, '왕초보 탈출 30일 루틴', '웨이트 트레이닝 기초 완벽 가이드', 500000, 10, TRUE, TRUE, 'VISIBLE'),
                                                                                                                                                                                                          (1, 2, 1, 4, 11, '10kg 감량 보장 다이어트', '체계적인 식단과 유산소 병행', 700000, 15, TRUE, TRUE, 'VISIBLE'),
                                                                                                                                                                                                          (2, 3, 2, 2, 12, '인생 바프 만들기 프로젝트', '바프 준비를 위한 고강도 트레이닝', 1200000, 20, TRUE, TRUE, 'VISIBLE'),
                                                                                                                                                                                                          (2, 4, 4, 3, 13, '거북목 라운드숄더 교정', '체형 교정과 통증 완화 집중', 800000, 12, FALSE, TRUE, 'VISIBLE'),
                                                                                                                                                                                                          (3, 5, 1, 4, 14, '직장인 단기 다이어트', '바쁜 직장인을 위한 효율적인 운동', 600000, 10, TRUE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (3, 6, 2, 1, 15, '스트렝스 향상 클래스', '3대 운동 중량 증가 집중 훈련', 900000, 15, FALSE, TRUE, 'VISIBLE'),
                                                                                                                                                                                                          (1, 1, 1, 1, 10, '한 달 20kg 감량 기적의 약물 PT', '불법 다이어트 약 처방 및 단기 속성 강제 감량', 1500000, 10, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (2, 3, 2, 2, 11, '여성 회원만 받습니다 (사심 PT)', '오빠가 친절하게 알려줄게 ^^', 50000, 10, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (3, 5, 3, 3, 12, '직장인 체형교정 8주 코스', '퇴근 후 거북목 탈출 프로젝트', 800000, 16, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (1, 2, 4, 4, 13, '수술 후 재활 전문 트레이닝', '병원 연계 안전한 재활 운동', 1200000, 20, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (2, 4, 1, 1, 14, '웨이트 트레이닝 정석 A to Z', '3대 운동 완벽 마스터', 900000, 15, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (3, 6, 2, 2, 15, '크로스핏 스타일 고강도 다이어트', '숨이 턱끝까지 차오르는 짜릿함', 700000, 12, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (1, 1, 3, 3, 10, '초보자를 위한 머신 사용법', '헬스장 기구 100% 활용하기', 400000, 8, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (2, 3, 4, 4, 11, '시니어 건강 맞춤형 PT', '50대 이상을 위한 관절 보호 운동', 850000, 15, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (3, 5, 1, 1, 12, '결혼 준비 신부 다이어트', '드레스 라인 만들기', 1100000, 20, FALSE, FALSE, 'VISIBLE'),
                                                                                                                                                                                                          (1, 2, 2, 2, 13, '파워리프팅 입문반', '스트렝스 훈련 가이드', 950000, 10, FALSE, FALSE, 'VISIBLE');

INSERT INTO pt_course_schedules (pt_course_id, day_of_week, start_time, end_time) VALUES
                                                                                      (1, 'MONDAY', '10:00:00', '11:00:00'), (1, 'WEDNESDAY', '10:00:00', '11:00:00'),
                                                                                      (2, 'TUESDAY', '19:00:00', '20:00:00'), (2, 'THURSDAY', '19:00:00', '20:00:00'),
                                                                                      (3, 'SATURDAY', '14:00:00', '15:00:00'), (3, 'SUNDAY', '14:00:00', '15:00:00');

INSERT INTO pt_curriculums (pt_course_id, session_no, title, content) VALUES
                                                                          (1, 1, '오리엔테이션 및 체형 분석', '인바디 측정 및 기초 근력 테스트 진행'),
                                                                          (1, 2, '스쿼트 기초', '맨몸 스쿼트 및 고관절 스트레칭, 무게중심 잡기'),
                                                                          (2, 1, '다이어트 식단 설계', '개인 기초대사량 기반 매크로 영양소 산출'),
                                                                          (7, 1, '약물 복용법', '식전 3알 드세요'), (8, 1, '연락처 교환', '수업 끝나고 술 한잔?'),
                                                                          (9, 1, '체형 평가', '라운드 숄더 확인'), (10, 1, '재활 1단계', '통증 유발점 마사지'),
                                                                          (11, 1, '데드리프트 기초', '힙힌지 만들기'), (12, 1, '체력 테스트', '버피테스트 100개'),
                                                                          (13, 1, '머신 세팅법', '중량 설정'), (14, 1, '가동범위 체크', '고관절 움직임 확인'),
                                                                          (15, 1, '상체 라인', '덤벨 숄더프레스'), (16, 1, '룰 미팅', '파워리프팅 대회 규정');

-- ---------------------------------------------------------
-- 8. PT 예약, 피드백, 리뷰, 캘린더, 운동 일지
-- ---------------------------------------------------------
INSERT INTO pt_reservations (user_id, pt_course_id, organization_id, trainer_profile_id, reserved_start_at, reserved_end_at, progress_count, total_session_count, status) VALUES
                                                                                                                                                                              (1, 1, 1, 1, '2026-06-01 10:00:00', '2026-06-01 11:00:00', 0, 10, 'RESERVED'),
                                                                                                                                                                              (2, 2, 1, 2, '2026-06-02 19:00:00', '2026-06-02 20:00:00', 0, 15, 'RESERVED'),
                                                                                                                                                                              (1, 7, 1, 1, '2026-06-10 10:00:00', '2026-06-10 11:00:00', 1, 10, 'COMPLETED'),
                                                                                                                                                                              (2, 8, 2, 3, '2026-06-11 19:00:00', '2026-06-11 20:00:00', 1, 10, 'COMPLETED'),
                                                                                                                                                                              (3, 9, 3, 5, '2026-06-12 14:00:00', '2026-06-12 15:00:00', 1, 16, 'COMPLETED'),
                                                                                                                                                                              (4, 10, 1, 2, '2026-06-13 15:00:00', '2026-06-13 16:00:00', 1, 20, 'COMPLETED'),
                                                                                                                                                                              (5, 11, 2, 4, '2026-06-14 10:00:00', '2026-06-14 11:00:00', 1, 15, 'COMPLETED'),
                                                                                                                                                                              (6, 12, 3, 6, '2026-06-15 20:00:00', '2026-06-15 21:00:00', 1, 12, 'COMPLETED'),
                                                                                                                                                                              (7, 13, 1, 1, '2026-06-16 11:00:00', '2026-06-16 12:00:00', 1, 8, 'COMPLETED'),
                                                                                                                                                                              (8, 14, 2, 3, '2026-06-17 13:00:00', '2026-06-17 14:00:00', 1, 15, 'COMPLETED'),
                                                                                                                                                                              (9, 15, 3, 5, '2026-06-18 19:00:00', '2026-06-18 20:00:00', 1, 20, 'COMPLETED'),
                                                                                                                                                                              (10, 16, 1, 2, '2026-06-19 14:00:00', '2026-06-19 15:00:00', 1, 10, 'COMPLETED');

INSERT INTO feedbacks (pt_reservation_id, pt_curriculum_id, trainer_profile_id, user_id, content, status) VALUES
                                                                                                              (1, 1, 1, 1, '오늘 첫 수업 수고 많으셨습니다. 발목 유연성 확보를 위해 꾸준히 스트레칭 해주세요!', 'ACTIVE'),
                                                                                                              (3, 4, 1, 1, '약 꼭 제때 챙겨드세요 ^^ 그래야 단기 감량 효과 봅니다.', 'ACTIVE'),
                                                                                                              (4, 5, 3, 2, '오늘 운동복 의상 너무 예쁘시네요. 수업에 집중하기 힘들었습니다 ^^;', 'ACTIVE'),
                                                                                                              (5, 6, 5, 3, '라운드 숄더 개선을 위해 평소 흉추 펴는 습관 잊지마세요!', 'ACTIVE'),
                                                                                                              (6, 7, 2, 4, '마사지 부위 폼롤러로 꼭 풀어주세요.', 'ACTIVE'),
                                                                                                              (7, 8, 4, 5, '힙힌지 동작 완벽했습니다. 다음엔 중량 올려볼게요.', 'ACTIVE'),
                                                                                                              (8, 9, 6, 6, '체력 진짜 좋으시네요! 다음엔 더 빡세게 갑니다.', 'ACTIVE'),
                                                                                                              (9, 10, 1, 7, '등 운동 시 승모근 개입 안되게 주의해주세요.', 'ACTIVE'),
                                                                                                              (10, 11, 3, 8, '어머님 오늘 무릎 괜찮으셨나요? 무리하지 않게 조절하겠습니다.', 'ACTIVE'),
                                                                                                              (11, 12, 5, 9, '식단 지켜주셔서 감사합니다. 라인이 보이기 시작했어요.', 'ACTIVE'),
                                                                                                              (12, 13, 2, 10, '호흡법 조금만 더 신경써주세요. 훌륭합니다.', 'ACTIVE');

INSERT INTO feedback_media (feedback_id, file_id, media_type) VALUES (1, 16, 'VIDEO');

INSERT INTO trainer_reviews (user_id, trainer_profile_id, pt_course_id, pt_reservation_id, rating, content, status) VALUES
                                                                                                                        (1, 1, 1, 1, 5, '설명도 너무 잘해주시고 자세가 금방 좋아졌어요. 강력 추천합니다!', 'ACTIVE'),
                                                                                                                        (1, 1, 7, 3, 1, '트레이너가 불법 약물을 강요합니다. 절대 가지마세요.', 'ACTIVE'),
                                                                                                                        (2, 3, 8, 4, 1, '운동은 안가르쳐주고 계속 몸매 평가하면서 추행합니다.', 'ACTIVE'),
                                                                                                                        (3, 5, 9, 5, 5, '거북목이 많이 좋아졌어요! 감사합니다.', 'ACTIVE'),
                                                                                                                        (4, 2, 10, 6, 5, '무릎 통증이 사라졌습니다.', 'ACTIVE'),
                                                                                                                        (5, 4, 11, 7, 4, '설명을 아주 잘해주십니다.', 'ACTIVE'),
                                                                                                                        (6, 6, 12, 8, 5, '너무 힘들지만 살은 확실히 빠집니다 ㅋㅋ', 'ACTIVE'),
                                                                                                                        (7, 1, 13, 9, 4, '기구 사용법 완벽히 익혔습니다.', 'ACTIVE'),
                                                                                                                        (8, 3, 14, 10, 5, '부모님 끊어드렸는데 만족하십니다.', 'ACTIVE'),
                                                                                                                        (9, 5, 15, 11, 5, '웨딩 드레스 입을 생각에 설레네요!', 'ACTIVE'),
                                                                                                                        (10, 2, 16, 12, 4, '3대 중량 50kg 올랐습니다.', 'ACTIVE');

INSERT INTO calendar_entries (user_id, entry_type, target_id, title, entry_date) VALUES
    (1, 'PT', 1, '스쿼트 기초 수업 (득근맨 트레이너)', '2026-06-01');

INSERT INTO workout_diaries (user_id, category_id, feedback_id, title, content, diary_date) VALUES
    (1, 2, 1, '오운완 - 하체 부수는 날', '스쿼트 50kg 5x5 성공! 강사님이 알려주신 폼 롤러로 풀고 시작함.', '2026-06-01');

-- ---------------------------------------------------------
-- 9. 커뮤니티 (게시글, 댓글, 좋아요)
-- ---------------------------------------------------------
INSERT INTO posts (user_id, post_type, title, content, view_count, like_count, comment_count, status) VALUES
                                                                                                          (1, 'NORMAL', '요즘 다이어트 식단 공유해요~', '아침엔 오트밀, 점심엔 닭가슴살 볶음밥 먹고 있습니다.', 125, 2, 1, 'VISIBLE'),
                                                                                                          (13, 'NORMAL', '바디프로필 D-30 꿀팁', '수분 조절은 필수입니다. 멘탈 관리 잘하세요!', 340, 15, 0, 'VISIBLE'),
                                                                                                          (1, 'NORMAL', '도박 사이트 가입하세요', '추천인 코드: xyz ...', 10, 0, 0, 'VISIBLE'),
                                                                                                          (2, 'NORMAL', '진짜 짜증나네', '헬스장 기구 독점하는 새끼들 뚝배기 깨고싶다', 15, 0, 0, 'VISIBLE'),
                                                                                                          (3, 'NORMAL', '오늘 등 운동 루틴 공유', '풀업 5세트, 랫풀다운 5세트...', 20, 5, 0, 'VISIBLE'),
                                                                                                          (4, 'NORMAL', '닭가슴살 질릴 때 꿀팁', '스리라차 소스가 최고입니다.', 50, 10, 0, 'VISIBLE'),
                                                                                                          (5, 'NORMAL', '운동 파트너 구해요 (성남)', '저녁 8시 을지대 근처에서 같이 하실 분!', 30, 2, 0, 'VISIBLE'),
                                                                                                          (6, 'NORMAL', '인바디 결과 충격이네요', '체지방률 30% 실화입니까...', 100, 4, 0, 'VISIBLE'),
                                                                                                          (7, 'NORMAL', '바프 준비 1일차', '식단 기록용으로 매일 올릴 예정입니다.', 12, 1, 0, 'VISIBLE'),
                                                                                                          (8, 'NORMAL', '스트랩 추천 좀 해주세요', '베르사그립 살까요? wsf 살까요?', 45, 0, 0, 'VISIBLE'),
                                                                                                          (9, 'NORMAL', '오늘 하체 완료', '오운완! 다리가 후들거리네요.', 80, 12, 0, 'VISIBLE'),
                                                                                                          (10, 'NORMAL', '운동 휴식일엔 뭐하시나요?', '저는 폼롤러로 마사지만 해줍니다.', 22, 3, 0, 'VISIBLE');

INSERT INTO comments (post_id, user_id, content, status) VALUES
                                                             (1, 2, '오트밀 레시피 궁금해요!', 'ACTIVE'),
                                                             (3, 4, '불법 스포츠 토토 사이트 -> http://...', 'ACTIVE'),
                                                             (4, 5, '글쓴이 진짜 ㅄ인가 ㅋㅋ 맛알못이네', 'ACTIVE'),
                                                             (5, 1, '저요! 쪽지 드렸습니다.', 'ACTIVE'),
                                                             (6, 2, '할 수 있습니다! 화이팅!', 'ACTIVE'),
                                                             (7, 3, '응원합니다. 득근하세요.', 'ACTIVE'),
                                                             (8, 4, '저는 베르사그립 강력 추천이요.', 'ACTIVE'),
                                                             (9, 5, '수고하셨습니다~', 'ACTIVE'),
                                                             (10, 6, '저도 폼롤러하고 푹 쉽니다.', 'ACTIVE'),
                                                             (3, 7, '루틴 좋네요 참고하겠습니다.', 'ACTIVE'),
                                                             (4, 8, '스리라차 최고죠 ㅎㅎ', 'ACTIVE');

INSERT INTO post_likes (post_id, user_id) VALUES (1, 2), (1, 3);

-- ---------------------------------------------------------
-- 10. 채팅 (채팅방 및 메시지)
-- ---------------------------------------------------------
INSERT INTO chat_rooms (user_id, trainer_id, pt_course_id, status, last_message_at) VALUES (1, 11, 1, 'ACTIVE', '2026-06-14 10:05:00');
INSERT INTO chat_messages (chat_room_id, sender_id, content, is_read) VALUES
    (1, 1, '트레이너님 안녕하세요! 내일 준비물 따로 있을까요?', true),
    (1, 11, '유저1님 안녕하세요! 개인 실내 운동화랑 개인 물통만 챙겨와주시면 됩니다^^', false);

-- ---------------------------------------------------------
-- 11. 신고, 블랙리스트, 관리자 액션 로그, 알림, 시스템 로그
-- (그룹당 신고 누적 4~6건 확장, PT_COURSE 3그룹 추가)
-- ---------------------------------------------------------
INSERT INTO report_groups (report_number, target_type, target_id, target_owner_id, snapshot_title, snapshot_content, snapshot_file_url, total_report_count, effective_report_count, review_status) VALUES
                                                                                                                                                                                                       ('REP-202606-001', 'POST', 1, 1, NULL, NULL, NULL, 4, 4, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-P01', 'POST', 3, 1, '도박 사이트 가입하세요', '추천인 코드: xyz ...', NULL, 5, 5, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-P02', 'POST', 4, 2, '진짜 짜증나네', '헬스장 기구 독점하는 새끼들 뚝배기 깨고싶다', NULL, 6, 6, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-C01', 'COMMENT', 2, 4, NULL, '불법 스포츠 토토 사이트 -> http://...', NULL, 4, 4, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-C02', 'COMMENT', 3, 5, NULL, '글쓴이 진짜 ㅄ인가 ㅋㅋ 맛알못이네', NULL, 5, 5, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-T01', 'PT_COURSE', 7, 11, '한 달 20kg 감량 기적의 약물 PT', '불법 다이어트 약 처방 및 단기 속성 강제 감량', 'https://s3.gymjjak.com/uuid-thumb1.jpg', 6, 6, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-T02', 'PT_COURSE', 8, 13, '여성 회원만 받습니다 (사심 PT)', '오빠가 친절하게 알려줄게 ^^', 'https://s3.gymjjak.com/uuid-thumb3.jpg', 4, 4, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-R01', 'TRAINER_REVIEW', 2, 1, NULL, '트레이너가 불법 약물을 강요합니다. 절대 가지마세요.', NULL, 5, 5, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-R02', 'TRAINER_REVIEW', 3, 2, NULL, '운동은 안가르쳐주고 계속 몸매 평가하면서 추행합니다.', NULL, 6, 6, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-F01', 'FEEDBACK', 2, 11, NULL, '약 꼭 제때 챙겨드세요 ^^ 그래야 단기 감량 효과 봅니다.', 'https://s3.gymjjak.com/uuid-fb1.mp4', 4, 4, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-F02', 'FEEDBACK', 3, 13, NULL, '오늘 운동복 의상 너무 예쁘시네요. 수업에 집중하기 힘들었습니다 ^^;', NULL, 5, 5, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-T03', 'PT_COURSE', 9, 15, '직장인 체형교정 8주 코스', '퇴근 후 거북목 탈출 프로젝트', 'https://s3.gymjjak.com/uuid-thumb12.jpg', 6, 6, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-T04', 'PT_COURSE', 10, 12, '수술 후 재활 전문 트레이닝', '병원 연계 안전한 재활 운동', 'https://s3.gymjjak.com/uuid-thumb13.jpg', 4, 4, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-T05', 'PT_COURSE', 11, 14, '웨이트 트레이닝 정석 A to Z', '3대 운동 완벽 마스터', 'https://s3.gymjjak.com/uuid-thumb14.jpg', 5, 5, 'PENDING');

INSERT INTO reports (report_group_id, reporter_id, reason, detail, status) VALUES
-- Group 1 (POST 1)
(1, 2, 'SPAM', '광고성 게시글인 것 같습니다.', 'PENDING'), (1, 3, 'SPAM', '스팸 글 도배 중입니다.', 'PENDING'), (1, 4, 'SPAM', '불법 사이트 유도', 'PENDING'), (1, 5, 'SPAM', '도용 의심 및 광고', 'PENDING'),
-- Group 2 (POST 3)
(2, 4, 'SPAM', '불법 도박 사이트 광고입니다.', 'PENDING'), (2, 5, 'SPAM', '신고합니다. 광고충이네요.', 'PENDING'), (2, 6, 'SPAM', '삭제 부탁드립니다.', 'PENDING'), (2, 7, 'SPAM', '스팸입니다.', 'PENDING'), (2, 8, 'SPAM', '이런 글 좀 못 쓰게 막아주세요.', 'PENDING'),
-- Group 3 (POST 4)
(3, 1, 'ABUSE', '심한 욕설 및 혐오 조장', 'PENDING'), (3, 3, 'ABUSE', '단어 선택이 너무 공격적입니다.', 'PENDING'), (3, 5, 'ABUSE', '보기 불편한 욕설이 포함되어 있습니다.', 'PENDING'), (3, 7, 'ABUSE', '커뮤니티 분위기를 흐립니다.', 'PENDING'), (3, 8, 'ABUSE', '공격적인 성향의 글', 'PENDING'), (3, 9, 'ABUSE', '욕설 제재 부탁드립니다.', 'PENDING'),
-- Group 4 (COMMENT 2)
(4, 1, 'SPAM', '스팸성 외부 링크 유도 댓글', 'PENDING'), (4, 2, 'SPAM', '악성 링크입니다.', 'PENDING'), (4, 3, 'SPAM', '토토 사이트 광고', 'PENDING'), (4, 5, 'SPAM', '이 유저 계속 광고 달고 다녀요.', 'PENDING'),
-- Group 5 (COMMENT 3)
(5, 1, 'ABUSE', '비하 발언 및 인신 공격', 'PENDING'), (5, 2, 'ABUSE', '댓글로 욕설을 하네요.', 'PENDING'), (5, 4, 'ABUSE', '작성자 비하 발언', 'PENDING'), (5, 6, 'ABUSE', '욕설 신고', 'PENDING'), (5, 7, 'ABUSE', '매너 좀 지켰으면 좋겠습니다.', 'PENDING'),
-- Group 6 (PT_COURSE 7)
(6, 1, 'ETC', '약사법 위반 약물 강요 의심', 'PENDING'), (6, 2, 'ETC', '다이어트 보조제를 반강제로 판매하려 합니다.', 'PENDING'), (6, 3, 'ETC', '부작용 심한 약물을 권유합니다.', 'PENDING'), (6, 4, 'ETC', '건강을 해치는 불법 PT입니다.', 'PENDING'), (6, 5, 'ETC', '의학적 검증 없는 약물 추천', 'PENDING'), (6, 8, 'ETC', '신고합니다. 위험해 보여요.', 'PENDING'),
-- Group 7 (PT_COURSE 8)
(7, 2, 'SEXUAL_CONTENT', '강좌 설명이 불쾌합니다.', 'PENDING'), (7, 4, 'SEXUAL_CONTENT', '성희롱적인 뉘앙스가 있습니다.', 'PENDING'), (7, 6, 'SEXUAL_CONTENT', '소개글이 매우 불건전합니다.', 'PENDING'), (7, 8, 'SEXUAL_CONTENT', '사심 PT라니요, 제재해 주세요.', 'PENDING'),
-- Group 8 (TRAINER_REVIEW 2)
(8, 11, 'ETC', '악의적인 허위 리뷰 작성으로 업무 방해', 'PENDING'), (8, 12, 'ETC', '경쟁 업체의 고의적인 별점 테러 의심', 'PENDING'), (8, 14, 'ETC', '수업 한 번도 안 듣고 쓴 악플입니다.', 'PENDING'), (8, 15, 'ETC', '허위 사실 유포', 'PENDING'), (8, 16, 'ETC', '트레이너 명예훼손', 'PENDING'),
-- Group 9 (TRAINER_REVIEW 3)
(9, 11, 'ABUSE', '명예 훼손성 악플 리뷰', 'PENDING'), (9, 12, 'ABUSE', '인신공격성 리뷰입니다.', 'PENDING'), (9, 13, 'ABUSE', '도를 넘은 비방입니다.', 'PENDING'), (9, 14, 'ABUSE', '욕설이 너무 심하네요.', 'PENDING'), (9, 15, 'ABUSE', '리뷰로 쌍욕을 적어놨습니다.', 'PENDING'), (9, 16, 'ABUSE', '허위사실 및 비방', 'PENDING'),
-- Group 10 (FEEDBACK 2)
(10, 1, 'ETC', '피드백으로 불법 다이어트 약 복용 지시', 'PENDING'), (10, 3, 'ETC', '약물 복용을 강요하는 피드백', 'PENDING'), (10, 5, 'ETC', '위험한 약물을 자꾸 권유합니다.', 'PENDING'), (10, 7, 'ETC', '피드백 내용이 부적절함', 'PENDING'),
-- Group 11 (FEEDBACK 3)
(11, 2, 'SEXUAL_CONTENT', '성적 수치심을 유발하는 피드백 멘트', 'PENDING'), (11, 4, 'SEXUAL_CONTENT', '운동과 상관없는 불쾌한 피드백', 'PENDING'), (11, 6, 'SEXUAL_CONTENT', '성희롱성 발언이 포함되어 있습니다.', 'PENDING'), (11, 8, 'SEXUAL_CONTENT', '사적인 연락을 자꾸 요구합니다.', 'PENDING'), (11, 10, 'SEXUAL_CONTENT', '피드백 내용이 너무 징그럽습니다.', 'PENDING'),
-- Group 12 (PT_COURSE 9)
(12, 1, 'ETC', '체형교정 효과가 전혀 없는 과장 광고입니다.', 'PENDING'), (12, 2, 'ETC', '전문 자격증이 없는 것으로 의심됩니다.', 'PENDING'), (12, 3, 'ETC', '허위 스펙을 기재해 두었습니다.', 'PENDING'), (12, 4, 'ETC', '환불 규정을 어기고 잠수탔습니다.', 'PENDING'), (12, 5, 'ETC', '홍보 내용과 실제 수업이 완전히 다릅니다.', 'PENDING'), (12, 6, 'ETC', '돈만 받고 제대로 안 가르쳐줌', 'PENDING'),
-- Group 13 (PT_COURSE 10)
(13, 2, 'ETC', '의학적 지식 없이 무리한 중량을 요구합니다.', 'PENDING'), (13, 3, 'ETC', '재활 PT라더니 오히려 부상이 악화되었습니다.', 'PENDING'), (13, 5, 'ETC', '초보자에게 너무 위험하게 가르칩니다.', 'PENDING'), (13, 8, 'ETC', '기본적인 해부학 지식도 없어 보입니다.', 'PENDING'),
-- Group 14 (PT_COURSE 11)
(14, 1, 'ETC', '환불 거부 및 지속적인 수업 당일 취소', 'PENDING'), (14, 4, 'ETC', '트레이너가 자꾸 지각합니다.', 'PENDING'), (14, 6, 'ETC', '수업 시간에 핸드폰만 봅니다.', 'PENDING'), (14, 8, 'ETC', '수업 태도가 너무 불량합니다.', 'PENDING'), (14, 10, 'ETC', '회원 관리를 전혀 안 합니다.', 'PENDING');

INSERT INTO blacklists (user_id, admin_id, type, reason, source_type) VALUES
    (10, 17, 'SUSPENSION', '불법 도박 사이트 지속적 광고 도배', 'ADMIN');

INSERT INTO admin_action_logs (actor_type, admin_id, action_type, target_type, target_id, description) VALUES
    ('ADMIN', 17, 'BAN_USER', 'USER', 10, '악성 유저(user10) 영구 정지 처리');

INSERT INTO notifications (receiver_id, notification_type, title, target_type, target_id) VALUES
    (1, 'CHAT', '득근맨 트레이너님으로부터 새 메시지가 도착했습니다.', 'CHAT_ROOM', 1);

INSERT INTO system_logs (trace_id, log_level, request_uri, message) VALUES
    ('trace-start-xyz123', 'INFO', '/api/v1/health', 'GymJjak System Application Started Successfully.');

SET FOREIGN_KEY_CHECKS = 1;
