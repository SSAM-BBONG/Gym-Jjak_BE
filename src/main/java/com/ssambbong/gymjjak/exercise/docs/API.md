# 🏃 운동 종목 API

> 대상: `AdminExerciseController` · 공통 경로: `/api/exercises`

| API | 권한 | 역할 | 성공 code |
| --- | --- | --- | --- |
| `POST /api/exercises` | ADMIN | 운동 종목 등록 | `EXERCISE_CREATED` |
| `PATCH /api/exercises/{exerciseId}` | ADMIN | 운동 이름 수정 | `EXERCISE_UPDATED` |
| `DELETE /api/exercises/{exerciseId}` | ADMIN | 운동 종목 삭제 | `EXERCISE_DELETED` |
| `GET /api/exercises?part=&keyword=` | SecurityConfig 허용 권한 | 부위·검색어 조회 | `EXERCISES_FETCHED` |

## 등록·수정

```json
{ "part": "하체", "exerciseName": "와이드 스쿼트" }
```

- 운동 부위는 서비스의 한국어 부위명 매퍼가 `PartType`으로 변환합니다.
- 이름은 필수이며 최대 100자입니다.
- 동일 부위에서 같은 이름을 중복 등록할 수 없습니다.
- 수정은 운동 부위를 유지하고 이름만 바꿉니다.
- 등록 응답의 `data.exerciseId`가 생성 ID입니다.

## 조회 응답

```json
[
  {
    "exerciseId": 1,
    "part": "하체",
    "exerciseName": "와이드 스쿼트",
    "createdAt": "2026-07-21"
  }
]
```

`part`와 `keyword`는 선택값이며 둘을 함께 사용할 수 있습니다. 검색어는 앞뒤 공백을 제거합니다.

| 실패 코드 | 설명 |
| --- | --- |
| `EXERCISE_404_001` | 운동 종목이 없음 |
| `EXERCISE_409_001` | 같은 부위·이름의 종목이 이미 있음 |

## 문서 정보

- 업데이트일: `2026-07-21`

