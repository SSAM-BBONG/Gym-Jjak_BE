# 🏋️ PT 강습 API Flow

> 이 문서는 PT 강습 등록·조회·수정·삭제 API의 내부 흐름을 설명합니다.
> 예약 생성·취소 흐름은 [ptReservation/docs/API_FLOW.md](../../ptReservation/docs/API_FLOW.md)를 참고합니다.

---

## 1. PtCourseService가 담당하는 역할

| 구성요소 | 책임 |
| --- | --- |
| `PtCourseController` | 요청값 검증, 인증 사용자 ID 추출, Command/Query UseCase 전달 |
| `PtCourseCommandService` | PT 강습 등록·수정·상태변경·삭제 처리 |
| `PtCourseQueryService` | 목록·상세·통계·인기강습·예약가능날짜·슬롯 조회 |
| `TrainerProfileQueryPort` | trainer bc에서 트레이너 프로필 ID 및 소속 조직 조회 |
| `UserNicknameQueryPort` | user bc에서 수강생 닉네임 조회 |
| `ReviewQueryPort` | trainerReview bc에서 강사평 요약 조회 |
| `PtReservationCountQueryPort` | ptReservation bc에서 예약 수 집계 조회 |
| `PtCourseRetentionJob` | 일정 기간 경과한 BLOCKED 강습 자동 삭제 배치 |

---

## 2. PT 강습 등록 흐름

```text
트레이너
  → POST /api/pt-courses
  → PtCourseController
  → CreatePtCourseCommand
  → PtCourseCommandService
      1. TrainerProfileQueryPort로 트레이너 프로필 ID 조회
      2. 해당 트레이너가 요청 조직에 소속되어 있는지 검증 (PT_COURSE_004)
      3. 썸네일 파일 메타데이터 처리 (없으면 null)
      4. PtCourse 도메인 객체 생성 (VISIBLE 상태)
      5. 커리큘럼 및 스케줄 저장
  → CreatePtCourseResponse(ptCourseId)
```

---

## 3. PT 강습 목록·상세 조회 흐름

```text
사용자
  → GET /api/pt-courses
  → PtCourseQueryService
  → PtCourseRepository.findAllVisible()
  → 조직 정보, 트레이너 정보, 강사평 통계 JOIN 조회
  → PtCourseViewResponse 목록
```

```text
사용자
  → GET /api/pt-courses/{ptCourseId}
  → PtCourseQueryService
      1. PT 강습 상세 조회 (VISIBLE 강습만)
      2. ReviewQueryPort로 최근 강사평 3개 조회 (trainerReview bc)
  → PtCourseDetailResponse
```

---

## 4. PT 강습 수정 흐름

```text
트레이너
  → PATCH /api/pt-courses/{ptCourseId}
  → PtCourseCommandService
      1. PT 강습 조회 및 소유자 검증 (PT_COURSE_003)
      2. 수강생이 있으면 커리큘럼 수정 불가 (CURRICULUM_UPDATE_NOT_ALLOWED)
      3. 커리큘럼: id 있으면 수정, 없으면 신규 생성
      4. 스케줄: id 있으면 수정, 없으면 신규 생성
  → UpdatePtCourseResponse(ptCourseId)
```

---

## 5. PT 강습 삭제 흐름

```text
트레이너
  → PATCH /api/pt-courses/{ptCourseId}/status { "status": "HIDDEN" }
  → PtCourseCommandService
      1. PT 강습 조회 및 소유자 검증 (PT_COURSE_003)
      2. HIDDEN 요청 시 활성 예약(RESERVED/IN_PROGRESS)이 있으면 비활성화 불가 (PT_COURSE_011)
      3. 상태 변경
```

```text
트레이너
  → DELETE /api/pt-courses/{ptCourseId}
  → PtCourseCommandService
      1. PT 강습 조회 및 소유자 검증 (PT_COURSE_003)
      2. BLOCKED 상태이면 삭제 불가 (PT_COURSE_009)
      3. 활성 예약(RESERVED/IN_PROGRESS)이 있으면 삭제 불가 (PT_COURSE_010)
      4. soft delete 처리
```

---

## 6. 예약 가능 날짜·시간 슬롯 조회 흐름

```text
사용자
  → GET /api/pt-courses/{ptCourseId}/available-dates
  → PtCourseQueryService
      1. PT 강습 스케줄에서 요일 정보 조회
      2. 오늘부터 30일 내 해당 요일에 해당하는 날짜 목록 반환
  → AvailableDatesResponse(availableDates)
```

```text
사용자
  → GET /api/pt-courses/{ptCourseId}/available-time-slots?date=yyyy-MM-dd
  → PtCourseQueryService
      1. 해당 날짜의 요일에 등록된 스케줄 조회
      2. 해당 날짜에 이미 예약된 시간 슬롯 조회 (ptReservation bc)
      3. 스케줄 슬롯별 예약 가능 여부 판단
  → AvailableTimeSlotsResponse(date, timeSlots)
```

---

## 7. 타 도메인 개발자 체크포인트 ✅

1. PT 강습 등록 시 트레이너 소속 조직 검증은 `TrainerProfileQueryPort`를 통해 trainer bc에 위임합니다.
2. 강사평 통계(`averageRating`, `reviewCount`)는 목록 조회 시 JOIN 쿼리로 직접 집계하지 않고 trainerReview bc의 `ReviewQueryPort`를 통해 조회합니다.
3. 신고로 인한 `BLOCKED` 상태 변경은 report bc의 `PtCourseSanctionAdapter`가 처리합니다. PT 강습 상태 변경 흐름이 변경되면 해당 어댑터를 함께 확인합니다.

---

## 📝 문서 정보

- 작성일: `2026-07-21`
