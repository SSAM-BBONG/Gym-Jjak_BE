-- ============================================================
-- V2.15__refactor_workout_diaries_and_create_sets.sql
--
-- 운동 일지 구조 개편
--
-- 변경 전
--   workout_diaries
--   - 하루 1개의 운동 일지
--   - title, content 중심
--
-- 변경 후
--   workout_diaries
--   - 하루에 여러 운동 등록 가능
--   - 운동 부위와 운동 종류 저장
--
--   workout_diary_sets
--   - 운동별 여러 세트 저장
--   - 세트 순서, 중량, 횟수 저장
-- ============================================================


-- ------------------------------------------------------------
-- 1. 기존 운동 일지 데이터 전체 삭제
-- ------------------------------------------------------------

-- 현재 Flyway DB 세션의 Safe Updates 설정 보관
SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;

-- 전체 데이터 삭제를 위해 현재 세션에서만 비활성화
SET SQL_SAFE_UPDATES = 0;

DELETE FROM workout_diaries;

-- 기존 설정 복원
SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;


-- ------------------------------------------------------------
-- 2. feedback_id 외래 키 제거
--
-- 이전 실패한 migration에서 이미 제거됐을 수도 있으므로
-- 외래 키가 존재할 때만 제거한다.
-- ------------------------------------------------------------

SET @feedback_fk_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'workout_diaries'
      AND COLUMN_NAME = 'feedback_id'
      AND REFERENCED_TABLE_NAME IS NOT NULL
    LIMIT 1
);

SET @drop_feedback_fk_sql = IF(
    @feedback_fk_name IS NULL,
    'SELECT 1',
    CONCAT(
        'ALTER TABLE workout_diaries DROP FOREIGN KEY `',
        @feedback_fk_name,
        '`'
    )
);

PREPARE drop_feedback_fk_stmt
    FROM @drop_feedback_fk_sql;

EXECUTE drop_feedback_fk_stmt;

DEALLOCATE PREPARE drop_feedback_fk_stmt;


-- ------------------------------------------------------------
-- 3. user_id + diary_date 일반 인덱스 먼저 생성
--
-- 기존 UNIQUE 인덱스가 user_id 외래 키를 위한 인덱스로도
-- 사용되고 있으므로, 대체 인덱스를 먼저 생성해야 한다.
-- ------------------------------------------------------------

SET @normal_user_date_index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'workout_diaries'
      AND INDEX_NAME = 'idx_workout_diaries_user_date'
);

SET @create_normal_user_date_index_sql = IF(
    @normal_user_date_index_exists = 0,
    'CREATE INDEX idx_workout_diaries_user_date
         ON workout_diaries (user_id, diary_date)',
    'SELECT 1'
);

PREPARE create_normal_user_date_index_stmt
    FROM @create_normal_user_date_index_sql;

EXECUTE create_normal_user_date_index_stmt;

DEALLOCATE PREPARE create_normal_user_date_index_stmt;


-- ------------------------------------------------------------
-- 4. user_id + diary_date UNIQUE 인덱스 제거
--
-- 하루에 여러 개의 운동을 등록할 수 있도록
-- 기존 하루 1회 제한을 제거한다.
-- ------------------------------------------------------------

SET @user_date_unique_index_name = (
    SELECT INDEX_NAME
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'workout_diaries'
      AND NON_UNIQUE = 0
      AND INDEX_NAME <> 'PRIMARY'
    GROUP BY INDEX_NAME
    HAVING GROUP_CONCAT(
        COLUMN_NAME
        ORDER BY SEQ_IN_INDEX
        SEPARATOR ','
    ) = 'user_id,diary_date'
    LIMIT 1
);

SET @drop_user_date_unique_sql = IF(
    @user_date_unique_index_name IS NULL,
    'SELECT 1',
    CONCAT(
        'ALTER TABLE workout_diaries DROP INDEX `',
        @user_date_unique_index_name,
        '`'
    )
);

PREPARE drop_user_date_unique_stmt
    FROM @drop_user_date_unique_sql;

EXECUTE drop_user_date_unique_stmt;

DEALLOCATE PREPARE drop_user_date_unique_stmt;


-- ------------------------------------------------------------
-- 5. workout_diaries 테이블 구조 변경
--
-- workout_diary_id -> id
-- feedback_id      -> 제거
-- title            -> 제거
-- content          -> 제거
-- part             -> 추가
-- exercise         -> 추가
-- ------------------------------------------------------------

ALTER TABLE workout_diaries
    CHANGE COLUMN workout_diary_id
    id BIGINT NOT NULL AUTO_INCREMENT,

DROP COLUMN feedback_id,
    DROP COLUMN title,
    DROP COLUMN content,

    ADD COLUMN part VARCHAR(30) NOT NULL
        COMMENT '운동 부위 PartType enum'
        AFTER user_id,

    ADD COLUMN exercise VARCHAR(100) NOT NULL
        COMMENT '운동 종류'
        AFTER part;


-- ------------------------------------------------------------
-- 6. 기존 데이터를 삭제했으므로 AUTO_INCREMENT 초기화
-- ------------------------------------------------------------

ALTER TABLE workout_diaries
    AUTO_INCREMENT = 1;


-- ------------------------------------------------------------
-- 7. 운동 세트 테이블 생성
--
-- 운동 일지 하나에 여러 세트를 등록할 수 있다.
-- ------------------------------------------------------------

CREATE TABLE workout_diary_sets (
                                    id BIGINT NOT NULL AUTO_INCREMENT,

                                    workout_diary_id BIGINT NOT NULL
                                        COMMENT '운동 일지 ID',

                                    set_order INT NOT NULL
                                        COMMENT '세트 순서',

                                    weight DECIMAL(6, 2) NOT NULL
                                        COMMENT '운동 중량(kg)',

                                    reps INT NOT NULL
                                        COMMENT '반복 횟수',

                                    created_at DATETIME(6) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(6),

                                    updated_at DATETIME(6) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

                                    CONSTRAINT pk_workout_diary_sets
                                        PRIMARY KEY (id),

                                    CONSTRAINT fk_workout_diary_sets_workout_diary
                                        FOREIGN KEY (workout_diary_id)
                                            REFERENCES workout_diaries (id)
                                            ON DELETE CASCADE,

                                    CONSTRAINT uk_workout_diary_sets_diary_order
                                        UNIQUE (workout_diary_id, set_order),

                                    CONSTRAINT chk_workout_diary_sets_set_order
                                        CHECK (set_order > 0),

                                    CONSTRAINT chk_workout_diary_sets_weight
                                        CHECK (weight >= 0),

                                    CONSTRAINT chk_workout_diary_sets_reps
                                        CHECK (reps > 0),

                                    INDEX idx_workout_diary_sets_workout_diary_id (
        workout_diary_id
    )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;