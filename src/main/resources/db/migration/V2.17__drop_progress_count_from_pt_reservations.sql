-- progress_count는 COUNT 쿼리로 동적 계산하므로 저장 컬럼 제거
ALTER TABLE pt_reservations DROP CHECK chk_pt_reservations_progress;
ALTER TABLE pt_reservations DROP COLUMN progress_count;
