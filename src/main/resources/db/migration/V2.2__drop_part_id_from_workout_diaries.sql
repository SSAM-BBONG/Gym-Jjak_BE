-- workout_diaries에서 part_id(기존 category_id) FK/컬럼 제거
ALTER TABLE workout_diaries DROP FOREIGN KEY fk_workout_diaries_part;
ALTER TABLE workout_diaries DROP COLUMN part_id;
