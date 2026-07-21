# 📅 캘린더 API

> 대상: `CalendarController` · 공통 경로: `/api/calendar` · 인증 필요

## API 목록

| API | 역할 | 성공 code |
| --- | --- | --- |
| `POST /diaries` | 운동 일지 작성 | `DIARY_CREATED` |
| `PATCH /diaries/{workoutDiaryId}` | 운동 일지 수정 | `DIARY_UPDATED` |
| `DELETE /diaries/{workoutDiaryId}` | 운동 일지 삭제 | `DIARY_DELETED` |
| `GET /day?date=2026-07-21&targetUserId=` | 일별 캘린더 조회 | `CALENDAR_DAY_FETCHED` |
| `GET /month?year=2026&month=7&targetUserId=` | 월별 캘린더 조회 | `CALENDAR_FETCHED` |

## 운동 일지 작성

```json
{
  "diaryDate": "2026-07-21",
  "part": "하체",
  "exerciseId": 1,
  "sets": [
    { "setOrder": 1, "weight": 20, "reps": 10 }
  ]
}
```

- `diaryDate`, 한국어 운동 부위, 운동 ID와 한 개 이상의 세트가 필요합니다.
- 세트 순서는 1 이상이고 중복될 수 없으며, 무게는 0 이상, 횟수는 1 이상입니다.
- 성공 응답의 `data.workoutDiaryId`가 생성된 일지 ID입니다.
- 같은 날짜·운동 종목의 일지가 이미 있으면 `CALENDAR_409_001`입니다.

수정 요청은 날짜를 제외한 `part`, `exerciseId`, `sets`를 모두 전달하며 기존 세트 전체를 교체합니다. 수정·삭제는 인증 사용자가 소유한 일지만 처리합니다.

## 일별 조회

```json
{
  "date": "2026-07-21",
  "pts": [{ "date": "2026-07-21", "title": "하체 PT", "ptId": 5 }],
  "diaries": [{
    "workoutDiaryId": 10,
    "exerciseId": 1,
    "exercise": "스쿼트",
    "date": "2026-07-21",
    "part": "하체",
    "sets": [{ "setId": 100, "setOrder": 1, "weight": 20, "reps": 10 }]
  }]
}
```

## 월별 조회

```json
{
  "year": 2026,
  "month": 7,
  "days": [
    { "date": "2026-07-21", "pt": true, "diarySummary": "스쿼트 외 1개" }
  ]
}
```

`targetUserId`를 생략하면 본인 캘린더를 조회합니다. 다른 사용자의 캘린더는 해당 사용자를 담당하는 활성 PT 트레이너만 조회할 수 있으며, 아니면 `CALENDAR_403_001`입니다. 월은 1~12여야 합니다.

## 주요 실패 코드

| code | 의미 |
| --- | --- |
| `DIARY_404_001` | 소유한 일지를 찾을 수 없음 |
| `CALENDAR_404_002` | 운동 종목을 찾을 수 없음 |
| `CALENDAR_400_032` | 세트 순서 중복 |
| `CALENDAR_409_001` | 같은 날짜의 동일 운동 일지 중복 |
| `CALENDAR_403_001` | 타인 캘린더 접근 권한 없음 |

## 문서 정보

- 업데이트일: `2026-07-21`

