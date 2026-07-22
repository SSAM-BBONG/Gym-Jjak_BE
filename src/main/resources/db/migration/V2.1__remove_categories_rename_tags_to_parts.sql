-- 1. pt_courses에서 category_id FK/컬럼 제거
ALTER TABLE pt_courses DROP FOREIGN KEY fk_pt_courses_category;
ALTER TABLE pt_courses DROP COLUMN category_id;

-- 2. pt_courses에서 tag_id FK/인덱스 제거, part_id로 이름 변경
ALTER TABLE pt_courses DROP FOREIGN KEY fk_pt_courses_tag;
DROP INDEX idx_pt_courses_tag ON pt_courses;
ALTER TABLE pt_courses CHANGE tag_id part_id BIGINT NOT NULL;

-- 3. tags 테이블: PK 컬럼 tag_id → part_id, 테이블명 → parts
ALTER TABLE tags CHANGE tag_id part_id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE tags RENAME TO parts;

-- 4. workout_diaries에서 category_id FK 제거, part_id로 이름 변경
ALTER TABLE workout_diaries DROP FOREIGN KEY fk_workout_diaries_category;
ALTER TABLE workout_diaries CHANGE category_id part_id BIGINT NOT NULL;

-- 5. FK/인덱스 재설정
ALTER TABLE pt_courses ADD CONSTRAINT fk_pt_courses_part FOREIGN KEY (part_id) REFERENCES parts(part_id);
ALTER TABLE pt_courses ADD INDEX idx_pt_courses_part (part_id);
ALTER TABLE workout_diaries ADD CONSTRAINT fk_workout_diaries_part FOREIGN KEY (part_id) REFERENCES parts(part_id);

-- 6. categories 테이블 DROP
DROP TABLE categories;
