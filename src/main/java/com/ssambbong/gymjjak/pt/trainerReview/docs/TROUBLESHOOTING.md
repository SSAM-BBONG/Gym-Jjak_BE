# 트레이너 강사평 도메인 트러블슈팅

> 이 문서는 실제 코드 흐름 기반으로 작성됐습니다. 강사평은 수강생이 작성하며, PT 예약 완료 후에만 가능합니다.

---

## 1. 강사평 생성이 실패한다

| 에러코드 | 원인 | 조치 |
|---|---|---|
| REVIEW_005 (404) | PT 예약 조회 실패 | `ptReservationId`, `userId`, `ptCourseId` 조합으로 예약이 존재하는지 확인 |
| REVIEW_004 (400) | 예약이 완료(`completed == true`)가 아님 | 예약 상태가 `COMPLETED`인지 확인 |
| REVIEW_002 (409) | 동일 예약에 강사평 이미 존재 | `trainer_reviews` 테이블에서 `ptReservationId`로 활성 강사평 조회 |

---

## 2. 강사평 수정·삭제가 안 된다

| 에러코드 | 원인 | 조치 |
|---|---|---|
| REVIEW_001 (404) | 강사평 없거나 삭제된 상태 | `findActiveById()`는 소프트 삭제된 강사평을 반환하지 않음 |
| REVIEW_003 (403) | 본인 강사평이 아님 | 강사평의 `userId`와 요청 `userId` 불일치 |

---

## 3. 강사평 삭제 후 목록에서 여전히 보인다

강사평 삭제는 소프트 삭제(`review.delete()` → `trainerReviewRepository.save(review)`)입니다. 데이터는 남아 있고 `deletedAt` 등 삭제 표시만 추가됩니다. 목록 조회 쿼리가 삭제된 항목을 필터링하는지 확인합니다.

---

## 4. 강사평 작성 후 메트릭이 기록되지 않았다

`TrainerReviewMetricsPort.recordCreated/recordUpdated/recordDeleted`는 트랜잭션 커밋 이후(`afterCommit`)에 실행되며, 실패해도 강사평 처리 자체는 정상 완료됩니다. 메트릭 누락 시 `메트릭 기록 실패 - metric: recordCreated` warn 로그를 확인합니다.

---

## 5. 주요 로그 이벤트 키 목록

서비스 코드에 별도 로그 이벤트가 없습니다. 예외 발생 시 글로벌 예외 핸들러 로그와 에러코드를 함께 확인합니다.

| 로그 | 의미 |
|---|---|
| `메트릭 기록 실패 - metric: recordCreated` | 강사평 생성 메트릭 기록 실패 (강사평 저장은 완료됨) |
| `메트릭 기록 실패 - metric: recordUpdated` | 강사평 수정 메트릭 기록 실패 |
| `메트릭 기록 실패 - metric: recordDeleted` | 강사평 삭제 메트릭 기록 실패 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
