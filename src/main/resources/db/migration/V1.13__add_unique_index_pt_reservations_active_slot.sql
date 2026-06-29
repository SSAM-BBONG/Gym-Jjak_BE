-- 같은 PT 강습의 같은 시작 시각에 RESERVED 상태 예약이 중복 삽입되는 race condition 방지
-- CANCELLED/COMPLETED 상태는 NULL로 처리되어 유니크 제약 대상에서 제외됨 (MySQL NULL != NULL)
CREATE UNIQUE INDEX uk_pt_reservations_active_slot
    ON pt_reservations (
        (CASE WHEN status = 'RESERVED' THEN pt_course_id END),
        (CASE WHEN status = 'RESERVED' THEN reserved_start_at END)
    );
