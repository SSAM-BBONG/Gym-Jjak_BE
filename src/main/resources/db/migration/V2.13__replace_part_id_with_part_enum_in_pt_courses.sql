ALTER TABLE pt_courses DROP FOREIGN KEY fk_pt_courses_part;
ALTER TABLE pt_courses DROP INDEX idx_pt_courses_part;
ALTER TABLE pt_courses DROP COLUMN part_id;
ALTER TABLE pt_courses ADD COLUMN part ENUM('CHEST','BACK','SHOULDER','ARM','ABS','CORE','LEG','GLUTE','FULL_BODY') NOT NULL;
DROP TABLE parts;

-- pt_courses 테이블의 part_id 컬럼을 제거하고 part ENUM 컬럼으로 교체했습니다.
-- parts 테이블을 삭제했습니다.
-- 파일명: V2.13__replace_part_id_with_part_enum_in_pt_courses.sql
