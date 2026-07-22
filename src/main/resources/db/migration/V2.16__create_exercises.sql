-- ============================================================
-- 운동 종목 마스터 테이블 생성
--
-- 관리자가 운동 종목을 등록·수정·삭제한다.
-- 운동 부위는 Java의 PartType enum 값을 저장한다.
--
-- workout_diaries와는 외래 키를 연결하지 않는다.
-- 따라서 운동 종목을 Hard Delete해도
-- 기존 운동 일지 데이터에는 영향을 주지 않는다.
-- ============================================================

CREATE TABLE exercises (
                           id BIGINT NOT NULL AUTO_INCREMENT,

                           part VARCHAR(30) NOT NULL
                               COMMENT '운동 부위 PartType enum',

                           exercise_name VARCHAR(100) NOT NULL
                               COMMENT '운동 이름',

                           created_at DATETIME(6) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(6),

                           updated_at DATETIME(6) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

                           CONSTRAINT pk_exercises
                               PRIMARY KEY (id),

                           CONSTRAINT uk_exercises_part_name
                               UNIQUE (part, exercise_name),

                           INDEX idx_exercises_part (
        part
    )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;