SET FOREIGN_KEY_CHECKS = 0;

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
-- ---------------------------------------------------------
INSERT INTO users (username, password, name, nickname, phone, role, status, onboarding_completed) VALUES
                                                                                                      ('user01@test.com', 'hashed_pwd', '유저1', '닉네임1', '010-0000-0001', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user02@test.com', 'hashed_pwd', '유저2', '닉네임2', '010-0000-0002', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user03@test.com', 'hashed_pwd', '유저3', '닉네임3', '010-0000-0003', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user04@test.com', 'hashed_pwd', '유저4', '닉네임4', '010-0000-0004', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user05@test.com', 'hashed_pwd', '유저5', '닉네임5', '010-0000-0005', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('user06@test.com', 'hashed_pwd', '유저6', '닉네임6', '010-0000-0006', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('user07@test.com', 'hashed_pwd', '유저7', '닉네임7', '010-0000-0007', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('user08@test.com', 'hashed_pwd', '유저8', '닉네임8', '010-0000-0008', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('user09@test.com', 'hashed_pwd', '유저9', '닉네임9', '010-0000-0009', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('user10@test.com', 'hashed_pwd', '유저10', '닉네임10', '010-0000-0010', 'USER', 'ACTIVE', FALSE),
                                                                                                      ('trainer01@test.com', 'hashed_pwd', '트레이너1', '득근맨', '010-1111-0001', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer02@test.com', 'hashed_pwd', '트레이너2', '헬창인생', '010-1111-0002', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer03@test.com', 'hashed_pwd', '트레이너3', '바프장인', '010-1111-0003', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer04@test.com', 'hashed_pwd', '트레이너4', '재활마스터', '010-1111-0004', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer05@test.com', 'hashed_pwd', '트레이너5', '다이어터', '010-1111-0005', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('trainer06@test.com', 'hashed_pwd', '트레이너6', '운동은밥', '010-1111-0006', 'TRAINER', 'ACTIVE', TRUE),
                                                                                                      ('admin01@test.com', 'hashed_pwd', '관리자1', '어드민1', '010-9999-0001', 'ADMIN', 'ACTIVE', TRUE),
                                                                                                      ('admin02@test.com', 'hashed_pwd', '관리자2', '어드민2', '010-9999-0002', 'ADMIN', 'ACTIVE', TRUE),
                                                                                                      ('owner01@test.com', 'hashed_pwd', '원장1', '짐잭대표1', '010-2222-0001', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('owner02@test.com', 'hashed_pwd', '원장2', '짐잭대표2', '010-2222-0002', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('owner03@test.com', 'hashed_pwd', '원장3', '짐잭대표3', '010-2222-0003', 'USER', 'ACTIVE', TRUE),
                                                                                                      ('org01@test.com', 'hashed_pwd', '조직계정1', '조직계정1', '010-3333-0001', 'ORGANIZATION', 'ACTIVE', TRUE),
                                                                                                      ('org02@test.com', 'hashed_pwd', '조직계정2', '조직계정2', '010-3333-0002', 'ORGANIZATION', 'ACTIVE', TRUE),
                                                                                                      ('org03@test.com', 'hashed_pwd', '조직계정3', '조직계정3', '010-3333-0003', 'ORGANIZATION', 'ACTIVE', TRUE);

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
-- 4. 파일(Files) (올바른 file_type 적용)
-- ---------------------------------------------------------
INSERT INTO files (uploader_id, original_name, stored_name, file_url, content_type, file_size, file_type, status) VALUES
                                                                                                                      (19, 'license1.pdf', 'uuid-lic1.pdf', 'https://s3.gymjjak.com/uuid-lic1.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (20, 'license2.pdf', 'uuid-lic2.pdf', 'https://s3.gymjjak.com/uuid-lic2.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (21, 'license3.pdf', 'uuid-lic3.pdf', 'https://s3.gymjjak.com/uuid-lic3.pdf', 'application/pdf', 1024, 'BUSINESS_LICENSE', 'ACTIVE'),
                                                                                                                      (11, 'profile1.jpg', 'uuid-prof1.jpg', 'https://s3.gymjjak.com/uuid-prof1.jpg', 'image/jpeg', 2048, 'TRAINER_PROFILE', 'ACTIVE'),
                                                                                                                      (12, 'profile2.jpg', 'uuid-prof2.jpg', 'https://s3.gymjjak.com/uuid-prof2.jpg', 'image/jpeg', 2048, 'TRAINER_PROFILE', 'ACTIVE'),
                                                                                                                      (13, 'profile3.jpg', 'uuid-prof3.jpg', 'https://s3.gymjjak.com/uuid-prof3.jpg', 'image/jpeg', 2048, 'TRAINER_PROFILE', 'ACTIVE'),
                                                                                                                      (14, 'profile4.jpg', 'uuid-prof4.jpg', 'https://s3.gymjjak.com/uuid-prof4.jpg', 'image/jpeg', 2048, 'TRAINER_PROFILE', 'ACTIVE'),
                                                                                                                      (15, 'profile5.jpg', 'uuid-prof5.jpg', 'https://s3.gymjjak.com/uuid-prof5.jpg', 'image/jpeg', 2048, 'TRAINER_PROFILE', 'ACTIVE'),
                                                                                                                      (16, 'profile6.jpg', 'uuid-prof6.jpg', 'https://s3.gymjjak.com/uuid-prof6.jpg', 'image/jpeg', 2048, 'TRAINER_PROFILE', 'ACTIVE'),
                                                                                                                      (11, 'thumb1.jpg', 'uuid-thumb1.jpg', 'https://s3.gymjjak.com/uuid-thumb1.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (12, 'thumb2.jpg', 'uuid-thumb2.jpg', 'https://s3.gymjjak.com/uuid-thumb2.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (13, 'thumb3.jpg', 'uuid-thumb3.jpg', 'https://s3.gymjjak.com/uuid-thumb3.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (14, 'thumb4.jpg', 'uuid-thumb4.jpg', 'https://s3.gymjjak.com/uuid-thumb4.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (15, 'thumb5.jpg', 'uuid-thumb5.jpg', 'https://s3.gymjjak.com/uuid-thumb5.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (16, 'thumb6.jpg', 'uuid-thumb6.jpg', 'https://s3.gymjjak.com/uuid-thumb6.jpg', 'image/jpeg', 3072, 'PT_THUMBNAIL', 'ACTIVE'),
                                                                                                                      (1, 'feedback_vid1.mp4', 'uuid-fb1.mp4', 'https://s3.gymjjak.com/uuid-fb1.mp4', 'video/mp4', 15000, 'FEEDBACK_VIDEO', 'ACTIVE');

-- ---------------------------------------------------------
-- 5. 조직 (Organization Applications & Organizations) (status = 'ACCEPTED')
-- ---------------------------------------------------------
INSERT INTO organization_applications (applicant_user_id, requested_login_id, business_license_file_id, business_registration_number, business_name, representative_name, representative_phone, opening_date, road_address, latitude, longitude, status) VALUES
                                                                                                                                                                                                                                                             (19, 'org01@test.com', 1, '111-11-11111', '짐잭피트니스 본점', '원장1', '010-2222-0001', '2020-01-01', '경기 성남시 수정구 양지동 212', 37.4609960, 127.1651358, 'ACCEPTED'),
                                                                                                                                                                                                                                                             (20, 'org02@test.com', 2, '222-22-22222', '짐잭피트니스 남한산성점', '원장2', '010-2222-0002', '2021-05-05', '경기 성남시 수정구 양지동 213', 37.4615000, 127.1660000, 'ACCEPTED'),
                                                                                                                                                                                                                                                             (21, 'org03@test.com', 3, '333-33-33333', '짐잭피트니스 단대오거리점', '원장3', '010-2222-0003', '2022-10-10', '경기 성남시 수정구 양지동 214', 37.4590000, 127.1645000, 'ACCEPTED');

INSERT INTO organizations (organization_account_id, owner_user_id, application_id, business_license_file_id, business_registration_number, business_name, representative_name, representative_phone, opening_date, road_address, latitude, longitude, status) VALUES
                                                                                                                                                                                                                                                                  (22, 19, 1, 1, '111-11-11111', '짐잭피트니스 본점', '원장1', '010-2222-0001', '2020-01-01', '경기 성남시 수정구 양지동 212', 37.4609960, 127.1651358, 'ACTIVE'),
                                                                                                                                                                                                                                                                  (23, 20, 2, 2, '222-22-22222', '짐잭피트니스 남한산성점', '원장2', '010-2222-0002', '2021-05-05', '경기 성남시 수정구 양지동 213', 37.4615000, 127.1660000, 'ACTIVE'),
                                                                                                                                                                                                                                                                  (24, 21, 3, 3, '333-33-33333', '짐잭피트니스 단대오거리점', '원장3', '010-2222-0003', '2022-10-10', '경기 성남시 수정구 양지동 214', 37.4590000, 127.1645000, 'ACTIVE');

-- ---------------------------------------------------------
-- 6. 트레이너 프로필 및 자격증/수상 내역
-- ---------------------------------------------------------
INSERT INTO trainer_applications (user_id, profile_file_id, spec, introduction, status) VALUES
                                                                                            (11, 4, '생활체육지도자 2급', '안녕하세요. 득근맨입니다.', 'APPROVED'),
                                                                                            (12, 5, '건강운동관리사', '정확한 자세를 알려드립니다.', 'APPROVED'),
                                                                                            (13, 6, 'NASM-CPT', '바프 전문 트레이너입니다.', 'APPROVED'),
                                                                                            (14, 7, '재활치료사 면허', '통증 없는 운동을 지향합니다.', 'APPROVED'),
                                                                                            (15, 8, '생활체육지도자 1급', '다이어트 확실하게 시켜드립니다.', 'APPROVED'),
                                                                                            (16, 9, '크로스핏 레벨1', '체력 증진 전문입니다.', 'APPROVED');

INSERT INTO trainer_profiles (user_id, application_id, profile_file_id, display_name, spec, introduction, average_rating, review_count, status) VALUES
                                                                                                                                                    (11, 1, 4, '득근맨', '생활체육지도자 2급', '안녕하세요. 득근맨입니다.', 4.5, 10, 'ACTIVE'),
                                                                                                                                                    (12, 2, 5, '헬창인생', '건강운동관리사', '정확한 자세를 알려드립니다.', 4.8, 25, 'ACTIVE'),
                                                                                                                                                    (13, 3, 6, '바프장인', 'NASM-CPT', '바프 전문 트레이너입니다.', 5.0, 50, 'ACTIVE'),
                                                                                                                                                    (14, 4, 7, '재활마스터', '재활치료사 면허', '통증 없는 운동을 지향합니다.', 4.9, 30, 'ACTIVE'),
                                                                                                                                                    (15, 5, 8, '다이어터', '생활체육지도자 1급', '다이어트 확실하게 시켜드립니다.', 4.2, 5, 'ACTIVE'),
                                                                                                                                                    (16, 6, 9, '운동은밥', '크로스핏 레벨1', '체력 증진 전문입니다.', 4.7, 15, 'ACTIVE');

INSERT INTO trainer_application_certifications (application_id, name, issuer, acquired_date) VALUES (1, '생활체육지도자 2급', '국민체육진흥공단', '2020-05-10');
INSERT INTO trainer_certifications (trainer_profile_id, name, issuer, acquired_date) VALUES (1, '생활체육지도자 2급', '국민체육진흥공단', '2020-05-10');
INSERT INTO trainer_application_awards (application_id, competition_name, award_name, award_date) VALUES (3, '2023 WNGP', '스포츠모델 1위', '2023-09-15');
INSERT INTO trainer_awards (trainer_profile_id, competition_name, award_name, award_date) VALUES (3, '2023 WNGP', '스포츠모델 1위', '2023-09-15');
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
                                                                                                                                                                              (1, 1, 1, 1, '2026-06-01 10:00:00', '2026-06-01 11:00:00', 1, 10, 'COMPLETED'),
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
INSERT INTO chat_rooms (user_id, trainer_profile_id, pt_course_id, status) VALUES (1, 1, 1, 'ACTIVE');
INSERT INTO chat_messages (chat_room_id, sender_id, content) VALUES
                                                                 (1, 1, '트레이너님 안녕하세요! 내일 준비물 따로 있을까요?'),
                                                                 (1, 11, '유저1님 안녕하세요! 개인 실내 운동화랑 개인 물통만 챙겨와주시면 됩니다^^');

-- ---------------------------------------------------------
-- 11. 신고, 블랙리스트, 관리자 액션 로그, 알림, 시스템 로그
-- ---------------------------------------------------------
INSERT INTO report_groups (report_number, target_type, target_id, target_owner_id, snapshot_title, snapshot_content, snapshot_file_url, total_report_count, effective_report_count, review_status) VALUES
                                                                                                                                                                                                       ('REP-202606-001', 'POST', 1, 1, NULL, NULL, NULL, 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-P01', 'POST', 3, 1, '도박 사이트 가입하세요', '추천인 코드: xyz ...', NULL, 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-P02', 'POST', 4, 2, '진짜 짜증나네', '헬스장 기구 독점하는 새끼들 뚝배기 깨고싶다', NULL, 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-C01', 'COMMENT', 2, 4, NULL, '불법 스포츠 토토 사이트 -> http://...', NULL, 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-C02', 'COMMENT', 3, 5, NULL, '글쓴이 진짜 ㅄ인가 ㅋㅋ 맛알못이네', NULL, 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-T01', 'PT_COURSE', 7, 11, '한 달 20kg 감량 기적의 약물 PT', '불법 다이어트 약 처방 및 단기 속성 강제 감량', 'https://s3.gymjjak.com/uuid-thumb1.jpg', 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-T02', 'PT_COURSE', 8, 13, '여성 회원만 받습니다 (사심 PT)', '오빠가 친절하게 알려줄게 ^^', 'https://s3.gymjjak.com/uuid-thumb3.jpg', 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-R01', 'TRAINER_REVIEW', 2, 1, NULL, '트레이너가 불법 약물을 강요합니다. 절대 가지마세요.', NULL, 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-R02', 'TRAINER_REVIEW', 3, 2, NULL, '운동은 안가르쳐주고 계속 몸매 평가하면서 추행합니다.', NULL, 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-F01', 'FEEDBACK', 2, 11, NULL, '약 꼭 제때 챙겨드세요 ^^ 그래야 단기 감량 효과 봅니다.', 'https://s3.gymjjak.com/uuid-fb1.mp4', 1, 1, 'PENDING'),
                                                                                                                                                                                                       ('REP-202606-F02', 'FEEDBACK', 3, 13, NULL, '오늘 운동복 의상 너무 예쁘시네요. 수업에 집중하기 힘들었습니다 ^^;', NULL, 1, 1, 'PENDING');

-- (올바른 reason enum으로 변경하여 INSERT)
INSERT INTO reports (report_group_id, reporter_id, reason, detail, status) VALUES
                                                                               (1, 3, 'SPAM', '광고성 게시글인 것 같습니다.', 'PENDING'),
                                                                               (2, 5, 'SPAM', '불법 도박 사이트 광고입니다.', 'PENDING'),
                                                                               (3, 6, 'ABUSE', '심한 욕설 및 혐오 조장', 'PENDING'),
                                                                               (4, 1, 'SPAM', '스팸성 외부 링크 유도 댓글', 'PENDING'),
                                                                               (5, 2, 'ABUSE', '비하 발언 및 인신 공격', 'PENDING'),
                                                                               (6, 3, 'ETC', '약사법 위반 약물 강요 의심', 'PENDING'),
                                                                               (7, 4, 'SEXUAL_CONTENT', '강좌 설명이 불쾌합니다.', 'PENDING'),
                                                                               (8, 11, 'ETC', '악의적인 허위 리뷰 작성으로 업무 방해', 'PENDING'),
                                                                               (9, 13, 'ABUSE', '명예 훼손성 악플 리뷰', 'PENDING'),
                                                                               (10, 1, 'ETC', '피드백으로 불법 다이어트 약 복용 지시', 'PENDING'),
                                                                               (11, 2, 'SEXUAL_CONTENT', '성적 수치심을 유발하는 피드백 멘트', 'PENDING');

INSERT INTO blacklists (user_id, admin_id, type, reason, source_type) VALUES
    (10, 17, 'SUSPENSION', '불법 도박 사이트 지속적 광고 도배', 'ADMIN');
INSERT INTO admin_action_logs (actor_type, admin_id, action_type, target_type, target_id, description) VALUES
    ('ADMIN', 17, 'BAN_USER', 'USER', 10, '악성 유저(user10) 영구 정지 처리');
INSERT INTO notifications (receiver_id, notification_type, title, target_type, target_id) VALUES
    (1, 'CHAT', '득근맨 트레이너님으로부터 새 메시지가 도착했습니다.', 'CHAT_ROOM', 1);
INSERT INTO system_logs (trace_id, log_level, request_uri, message) VALUES
    ('trace-start-xyz123', 'INFO', '/api/v1/health', 'GymJjak System Application Started Successfully.');

SET FOREIGN_KEY_CHECKS = 1;