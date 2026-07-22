# PT 피드백 도메인 트러블슈팅

> 이 문서는 실제 코드 흐름 기반으로 작성됐습니다. 피드백은 트레이너만 작성 가능합니다.

---

## 1. 피드백 생성이 실패한다

| 에러코드 | 원인 | 조치 |
|---|---|---|
| FEEDBACK_007 (409) | 세션이 완료되지 않음 | 예약 `status == COMPLETED` 이거나 `reservedEndAt < 현재시각`이어야 함 |
| FEEDBACK_002 (403) | 트레이너 프로필 없거나 본인 강습 예약이 아님 | 요청 userId의 트레이너 프로필 확인, 예약의 trainerProfileId 비교 |
| FEEDBACK_003 (404) | 커리큘럼이 해당 PT 코스 소속이 아님 | ptCurriculumId와 예약의 ptCourseId 연관 확인 |
| FEEDBACK_004 (409) | 동일 예약+커리큘럼에 피드백 이미 존재 | `feedbacks` 테이블에서 `ptReservationId + ptCurriculumId` 조합 확인 |
| FEEDBACK_005 (400) | 미디어 타입 중복 또는 파일 필수값 누락 | BEFORE/AFTER 각 1개, 파일 key·name·contentType·size 모두 필수 |

---

## 2. 피드백 삭제가 안 된다

FEEDBACK_006 (409) — 예약 상태가 `COMPLETED`이면 피드백을 삭제할 수 없습니다.

완료된 예약의 피드백은 영구 보존됩니다. 수정이 필요하면 `PUT /api/pt-reservations/{id}/feedbacks/{feedbackId}`로 내용을 교체합니다.

---

## 3. 피드백 수정 시 미디어 파일이 모두 교체됐다

`updateFeedback()`은 기존 미디어를 전체 삭제 후 새 미디어로 교체합니다 (`feedbackMediaRepository.deleteAllByFeedbackId()` → `saveAll()`). 일부만 변경하는 것은 지원하지 않으며, 수정 요청 시 유지할 파일 포함 전체 미디어를 다시 전달해야 합니다.

---

## 4. 미디어 파일 등록 결과가 올바르지 않다

`event=feedback_media_register_failed reason=unexpected_result` 로그 확인.

`fileUseCase.registerFiles()` 반환 결과 건수가 요청 건수와 다를 때 발생합니다. `FileUseCase` 구현체 및 파일 서버 상태를 확인합니다.

---

## 5. 주요 로그 이벤트 키 목록

| 이벤트 키 | 의미 |
|---|---|
| `event=feedback_create userId= ptReservationId= ptCurriculumId=` | 피드백 생성 시작 (debug) |
| `event=feedback_create_complete feedbackId=` | 피드백 생성 성공 |
| `event=feedback_update_failed reason=not_found` | 피드백 없음 |
| `event=feedback_update_failed reason=forbidden` | 권한 없음 |
| `event=feedback_update_complete feedbackId=` | 피드백 수정 성공 |
| `event=feedback_delete_failed reason=not_found` | 피드백 없음 |
| `event=feedback_delete_failed reason=forbidden` | 권한 없음 |
| `event=feedback_delete_failed reason=reservation_completed` | 완료 예약 — 삭제 불가 |
| `event=feedback_delete_complete feedbackId=` | 피드백 삭제 성공 |
| `event=feedback_media_register_failed` | 미디어 파일 등록 실패 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
