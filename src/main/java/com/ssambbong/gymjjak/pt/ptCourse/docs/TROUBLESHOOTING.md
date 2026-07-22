# PT 코스 도메인 트러블슈팅

> 이 문서는 실제 코드 흐름 기반으로 작성됐습니다. 로그 이벤트 키로 원인을 추적합니다.

---

## 1. PT 코스 생성이 실패한다

`event=pt_course_create_failed` 로그의 `reason` 값으로 원인을 구분합니다.

| reason | 에러코드 | 원인 | 조치 |
|---|---|---|---|
| `no_curriculum` | — | 커리큘럼이 한 건도 없음 | 커리큘럼 1개 이상 필수 |
| `duplicate_session_no` | PT_COURSE_001 | 커리큘럼 세션 번호 중복 | 세션 번호를 고유하게 수정 |
| `no_schedule` | — | 스케줄이 한 건도 없음 | 스케줄 1개 이상 필수 |
| `duplicate_schedule` | PT_COURSE_001 | 동일 요일·시간 스케줄 중복 | 스케줄 중복 제거 |
| `trainer_not_in_org` | PT_COURSE_004 | 트레이너가 해당 조직 소속 아님 | 조직 소속 트레이너 확인 |
| `trainer_profile_not_found` | PT_COURSE_005 | 트레이너 프로필 조회 실패 | 트레이너 프로필 존재 여부 확인 |

썸네일 등록이 실패해도 코스 생성이 롤백될 수 있습니다 (`event=pt_course_thumbnail_register_failed`).

---

## 2. PT 코스 상태 변경이 안 된다

`event=pt_course_status_change_failed` 로그의 `reason` 값으로 원인을 구분합니다.

| reason | 에러코드 | 원인 | 조치 |
|---|---|---|---|
| `not_found` | PT_COURSE_002 | PT 코스 조회 실패 | ptCourseId 확인 |
| `forbidden` | PT_COURSE_003 | 본인 코스가 아님 | 요청 userId와 코스 소유자 일치 여부 확인 |
| `invalid_status` | PT_COURSE_006 | 허용되지 않은 상태 전이 | 도메인 상태 머신 규칙 확인 |
| `has_active_reservation_for_hide` | PT_COURSE_011 | HIDDEN 전환 시 활성 예약 존재 | 활성 예약(`RESERVED`/`IN_PROGRESS`) 처리 후 재시도 |

---

## 3. PT 코스 삭제가 안 된다

`event=pt_course_delete_failed` 로그의 `reason` 값으로 원인을 구분합니다.

| reason | 에러코드 | 원인 | 조치 |
|---|---|---|---|
| `not_found` | PT_COURSE_002 | PT 코스 조회 실패 | ptCourseId 확인 |
| `forbidden` | PT_COURSE_003 | 본인 코스가 아님 | 요청 userId 확인 |
| `cannot_delete` | PT_COURSE_009 | 코스 상태가 BLOCKED | BLOCKED 상태에서는 삭제 불가 |
| `has_active_reservation` | PT_COURSE_010 | 활성 예약(`RESERVED`/`IN_PROGRESS`) 존재 | 활성 예약 처리 후 재시도 |

---

## 4. 커리큘럼 수정이 안 된다

`PT_COURSE_008` (409) — 활성 예약(`RESERVED`/`IN_PROGRESS`)이 1건 이상 존재할 때 커리큘럼 수정이 차단됩니다.

활성 예약 수 확인:
```sql
SELECT COUNT(*) FROM pt_reservations
WHERE pt_course_id = ? AND status IN ('RESERVED', 'IN_PROGRESS');
```

---

## 5. PT 코스 목록 캐시가 갱신되지 않는다

PT 코스 생성·수정·상태 변경·삭제 시 `@CacheEvict(cacheNames = "ptCourseList", allEntries = true)`가 실행됩니다.

- 변경 작업 후에도 목록이 갱신되지 않는 것처럼 보이면 Redis를 직접 조회해 캐시 키 존재 여부를 확인합니다.
- 로컬 환경에서는 캐시 설정이 비활성화된 경우 캐시 이슈가 재현되지 않을 수 있습니다.

---

## 6. 주요 로그 이벤트 키 목록

| 이벤트 키 | 의미 |
|---|---|
| `event=pt_course_create_failed` | 코스 생성 실패 (reason 참고) |
| `event=pt_course_create_succeeded` | 코스 생성 성공 |
| `event=pt_course_update_failed` | 코스 수정 실패 (reason 참고) |
| `event=pt_course_update_succeeded` | 코스 수정 성공 |
| `event=pt_course_status_change_failed` | 상태 변경 실패 (reason 참고) |
| `event=pt_course_status_change_succeeded` | 상태 변경 성공 |
| `event=pt_course_delete_failed` | 코스 삭제 실패 (reason 참고) |
| `event=pt_course_delete_succeeded` | 코스 삭제 성공 |
| `event=pt_course_thumbnail_register_failed` | 썸네일 파일 등록 실패 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
