-- 성능 테스트 시드 데이터
-- dummy-data.sql 실행 후에 실행하세요
-- MySQL 5.7 / 8.0 모두 호환 (CTE 미사용)

USE gymjjak_db;

SET NAMES utf8mb4;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;

-- ================================================================
-- 재실행 대비 기존 시드 데이터 정리
-- ================================================================
DELETE FROM chat_messages;
DELETE FROM chat_rooms WHERE chat_room_id BETWEEN 2 AND 10;
DELETE FROM pt_courses WHERE pt_course_id BETWEEN 2 AND 10;
DELETE FROM organizations WHERE organization_account_id BETWEEN 10001 AND 10200;
DELETE FROM organization_applications WHERE organization_application_id BETWEEN 20001 AND 20200;
DELETE FROM organization_applications WHERE applicant_user_id = 19 AND requested_login_id LIKE 'perf_pending_%';
DELETE FROM organization_applications WHERE applicant_user_id = 19 AND requested_login_id LIKE 'perf_filler_%';
DELETE FROM users WHERE user_id BETWEEN 10001 AND 10200;

-- ================================================================
-- 숫자 생성용 인라인 뷰 정의 (cross join 방식, CTE 불필요)
-- n0: 0~9, 조합으로 원하는 범위 생성
-- ================================================================

-- ================================================================
-- 1. PENDING 조직신청 300건 (admin find_pending 부하용 — 현실적인 적체 규모)
--    applicant: user_id=19, file_id=1 재사용
-- ================================================================
INSERT INTO organization_applications
    (applicant_user_id, requested_login_id, business_license_file_id,
     business_registration_number, business_name, representative_name,
     representative_phone, opening_date, road_address, detail_address,
     latitude, longitude, public_data_verified, status, created_at, updated_at)
SELECT
    19,
    CONCAT('perf_pending_', n, '@test.com'),
    1,
    CONCAT(LPAD(n % 999 + 1, 3, '0'), '-', LPAD(n % 99 + 1, 2, '0'), '-', LPAD(n, 5, '0')),
    CONCAT('성능테스트짐 ', n),
    CONCAT('대표자', n),
    CONCAT('010-5', LPAD(FLOOR(n / 10000), 3, '0'), '-', LPAD(n % 10000, 4, '0')),
    '2022-01-01',
    CONCAT('서울 강남구 테헤란로 ', n, '길 10'),
    '1층',
    37.4609960 + (n % 500) * 0.0001,
    127.1651358 + (n % 500) * 0.0001,
    0, 'PENDING',
    DATE_SUB(NOW(6), INTERVAL n SECOND),
    DATE_SUB(NOW(6), INTERVAL n SECOND)
FROM (
    SELECT a.n + b.n * 10 + c.n * 100 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2) c
    HAVING n <= 300
    ORDER BY n
) AS seq;

-- ================================================================
-- 1-2. ACCEPTED/REJECTED 조직신청 49,450건 (테이블 전체 규모를 5만 건으로 키워
--       "1~2년 운영 후 이력 누적" 시나리오에서 풀스캔 비용을 재현하기 위함. org 생성과 무관)
-- ================================================================
INSERT INTO organization_applications
    (applicant_user_id, requested_login_id, business_license_file_id,
     business_registration_number, business_name, representative_name,
     representative_phone, opening_date, road_address, detail_address,
     latitude, longitude, public_data_verified, status, reviewed_by, reviewed_at, created_at, updated_at)
SELECT
    19,
    CONCAT('perf_filler_', n, '@test.com'),
    1,
    CONCAT(LPAD(n % 999 + 1, 3, '0'), '-', LPAD(n % 99 + 1, 2, '0'), '-', LPAD(900000 + n, 7, '0')),
    CONCAT('성능필러짐 ', n),
    CONCAT('필러대표', n),
    CONCAT('010-6', LPAD(FLOOR(n / 10000), 3, '0'), '-', LPAD(n % 10000, 4, '0')),
    '2021-01-01',
    CONCAT('서울 강남구 필러로 ', n, '길 10'),
    '1층',
    37.4609960 + (n % 500) * 0.0001,
    127.1651358 + (n % 500) * 0.0001,
    0,
    IF(n % 2 = 0, 'ACCEPTED', 'REJECTED'),
    17, NOW(6),
    DATE_SUB(NOW(6), INTERVAL n MINUTE),
    DATE_SUB(NOW(6), INTERVAL n MINUTE)
FROM (
    SELECT a.n + b.n * 10 + c.n * 100 + d.n * 1000 + e.n * 10000 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) e
    HAVING n <= 49450
    ORDER BY n
) AS seq;

-- ================================================================
-- 2. user_id=1(user01)의 신청 이력 50건 (user find_my 부하용)
-- ================================================================
INSERT INTO organization_applications
    (applicant_user_id, requested_login_id, business_license_file_id,
     business_registration_number, business_name, representative_name,
     representative_phone, opening_date, road_address, detail_address,
     latitude, longitude, public_data_verified, status, created_at, updated_at)
SELECT
    1,
    CONCAT('user01_app_', n, '@test.com'),
    1,
    CONCAT('777-', LPAD(n, 2, '0'), '-', LPAD(n, 5, '0')),
    CONCAT('내짐 신청 ', n),
    '일반회원',
    CONCAT('010-7', LPAD(n, 3, '0'), '-', LPAD(n, 4, '0')),
    '2023-01-01',
    CONCAT('경기 수원시 테스트로 ', n, '길'),
    NULL,
    37.2809960 + n * 0.0001,
    127.0451358 + n * 0.0001,
    0,
    ELT(1 + (n % 3), 'PENDING', 'ACCEPTED', 'REJECTED'),
    DATE_SUB(NOW(6), INTERVAL n HOUR),
    DATE_SUB(NOW(6), INTERVAL n HOUR)
FROM (
    SELECT a.n + b.n * 10 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) b
    HAVING n <= 50
    ORDER BY n
) AS seq;

-- ================================================================
-- 3. 조직 계정용 사용자 200명 (user_id: 10001~10200)
--    username, nickname, phone UNIQUE 보장
-- ================================================================
INSERT INTO users
    (user_id, username, password, name, nickname, phone,
     role, status, onboarding_completed, created_at, updated_at)
SELECT
    10000 + n,
    CONCAT('perf_org_', n, '@test.com'),
    '$2a$10$XmQElXImWxbrr.5RaOWYXOHzFTxSDDgM28zJywuEKV9QFCILL5ga6',
    CONCAT('성능조직', n),
    CONCAT('성능짐', n),
    CONCAT('010-8', LPAD(n, 3, '0'), '-', LPAD(n, 4, '0')),
    'ORGANIZATION', 'ACTIVE', 1,
    DATE_SUB(NOW(6), INTERVAL n MINUTE),
    DATE_SUB(NOW(6), INTERVAL n MINUTE)
FROM (
    SELECT a.n + b.n * 10 + c.n * 100 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1) c
    HAVING n <= 200
    ORDER BY n
) AS seq;

-- ================================================================
-- 4. ACCEPTED 조직신청 200건 (org 생성용, ID: 20001~20200)
-- ================================================================
INSERT INTO organization_applications
    (organization_application_id, applicant_user_id, requested_login_id,
     business_license_file_id, business_registration_number, business_name,
     representative_name, representative_phone, opening_date, road_address,
     detail_address, latitude, longitude, public_data_verified,
     status, reviewed_by, reviewed_at, created_at, updated_at)
SELECT
    20000 + n,
    19,
    CONCAT('perf_org_', n, '@test.com'),
    1,
    CONCAT('888-', LPAD(n % 100, 2, '0'), '-', LPAD(n, 5, '0')),
    CONCAT('성능짐 ', n),
    CONCAT('대표', n),
    CONCAT('010-8', LPAD(n, 3, '0'), '-', LPAD(n, 4, '0')),
    '2022-06-01',
    CONCAT('서울 강남구 성능로 ', n, '길 ', n),
    '2층',
    37.4609960 + (n % 100) * 0.001,
    127.1651358 + (n % 100) * 0.001,
    0, 'ACCEPTED', 17, NOW(6),
    DATE_SUB(NOW(6), INTERVAL n MINUTE),
    DATE_SUB(NOW(6), INTERVAL n MINUTE)
FROM (
    SELECT a.n + b.n * 10 + c.n * 100 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1) c
    HAVING n <= 200
    ORDER BY n
) AS seq;

-- ================================================================
-- 5. 조직 200개 (AUTO_INCREMENT)
--    organization_account_id, application_id, BRN 모두 UNIQUE
-- ================================================================
INSERT INTO organizations
    (organization_account_id, owner_user_id, application_id,
     business_license_file_id, business_registration_number, business_name,
     representative_name, representative_phone, opening_date,
     road_address, detail_address, latitude, longitude,
     status, created_at, updated_at)
SELECT
    10000 + n,
    19,
    20000 + n,
    1,
    CONCAT('888-', LPAD(n % 100, 2, '0'), '-', LPAD(n, 5, '0')),
    CONCAT('성능짐 ', n),
    CONCAT('대표', n),
    CONCAT('010-8', LPAD(n, 3, '0'), '-', LPAD(n, 4, '0')),
    '2022-06-01',
    CONCAT('서울 강남구 성능로 ', n, '길 ', n),
    '2층',
    37.4609960 + (n % 100) * 0.001,
    127.1651358 + (n % 100) * 0.001,
    'ACTIVE',
    DATE_SUB(NOW(6), INTERVAL n MINUTE),
    DATE_SUB(NOW(6), INTERVAL n MINUTE)
FROM (
    SELECT a.n + b.n * 10 + c.n * 100 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1) c
    HAVING n <= 200
    ORDER BY n
) AS seq;

-- ================================================================
-- 6. 채팅방 250개(user_id=1, trainer_profile_id=1 동일 — pt_course_id로 구분)
--    × 메시지 500건 = 총 125,000건
--    "방당 실제 참여자는 2명(user01+trainer01)" 동시성 부하 시나리오 검증용
--    sender 교대: user_id=1 / trainer user_id=11
-- ================================================================

-- 재실행 대비 기존 채팅 데이터 초기화
DELETE FROM chat_messages;
DELETE FROM chat_rooms WHERE chat_room_id BETWEEN 2 AND 250;
DELETE FROM pt_courses WHERE pt_course_id BETWEEN 2 AND 250;

-- PT 강습 2~250 추가
INSERT INTO pt_courses
    (pt_course_id, organization_id, trainer_profile_id, category_id, tag_id,
     thumbnail_file_id, title, description, price, total_session_count,
     supports_diet_log, supports_workout_log, status, created_at, updated_at, deleted_at)
SELECT
    n + 1, 1, 1, 2, 1, 10,
    CONCAT('성능테스트 강습 ', n + 1),
    '성능 테스트용', 100000, 10, 0, 0, 'VISIBLE', NOW(6), NOW(6), NULL
FROM (
    SELECT a.n + b.n * 10 + c.n * 100 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2) c
    HAVING n <= 249
    ORDER BY n
) AS seq;

-- 채팅방 2~250 추가
INSERT INTO chat_rooms
    (chat_room_id, user_id, trainer_profile_id, pt_course_id, user_left, trainer_left,
     status, last_message_at, created_at, closed_at, updated_at)
SELECT
    n + 1, 1, 1, n + 1, 0, 0, 'ACTIVE', NOW(6), NOW(6), NULL, NOW(6)
FROM (
    SELECT a.n + b.n * 10 + c.n * 100 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2) c
    HAVING n <= 249
    ORDER BY n
) AS seq;

-- 채팅방 1~250에 각 500건 메시지 (총 125,000건)
INSERT INTO chat_messages (chat_room_id, sender_id, content, is_read, created_at)
SELECT
    room_id,
    IF(n % 2 = 0, 1, 11),
    CONCAT('성능 테스트 메시지 #', n, ' in room #', room_id),
    1,
    DATE_SUB(NOW(6), INTERVAL (room_id * 1000 + n) SECOND)
FROM (
    SELECT a.n + b.n * 10 + c.n * 100 + 1 AS room_id
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2) c
    HAVING room_id <= 250
) AS rooms
CROSS JOIN (
    SELECT a.n + b.n * 10 + c.n * 100 + 1 AS n
    FROM
        (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
        CROSS JOIN (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) c
    HAVING n <= 500
    ORDER BY n
) AS seq;

-- last_message_at 갱신
UPDATE chat_rooms SET last_message_at = NOW(6), updated_at = NOW(6) WHERE chat_room_id BETWEEN 1 AND 250;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

SELECT '성능 시드 완료' AS result,
       (SELECT COUNT(*) FROM organization_applications WHERE status = 'PENDING') AS pending_applications,
       (SELECT COUNT(*) FROM organizations)                                       AS total_organizations,
       (SELECT COUNT(*) FROM chat_rooms WHERE chat_room_id BETWEEN 1 AND 250)    AS chat_rooms_count,
       (SELECT COUNT(*) FROM chat_messages)                                       AS total_chat_messages;
