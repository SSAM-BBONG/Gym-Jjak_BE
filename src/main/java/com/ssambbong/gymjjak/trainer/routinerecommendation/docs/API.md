# 트레이너 수강생 루틴 추천 API

## POST `/api/trainers/members/{memberId}/routine-recommendations`

트레이너가 담당 수강생의 최근 28일 운동일지와 화면에서 입력한 일회성 프로필로 맞춤 루틴을 생성합니다.

- 권한: `TRAINER`
- `memberId`: 수강생 `userId`
- 입력 폼과 생성 결과는 저장하지 않습니다. DB 마이그레이션도 없습니다.
- 인바디·온보딩 정보는 이 API에서 조회하지 않습니다.

### Request Body

```json
{
  "gender": "MALE",
  "age": 28,
  "heightCm": 175.5,
  "weightKg": 72.3,
  "goal": "MUSCLE_GAIN"
}
```

| 필드 | 설명 | 제약 |
| --- | --- | --- |
| `gender` | 성별 | `MALE`, `FEMALE`, `UNSPECIFIED` |
| `age` | 만 나이 | 14~100 |
| `heightCm` | 키(cm) | 0.1~300 |
| `weightKg` | 몸무게(kg) | 0.1~500 |
| `goal` | 운동 목표 | `WEIGHT_LOSS`, `MUSCLE_GAIN`, `STRENGTH`, `HEALTH`, `REHABILITATION` |

### Response

`data`는 FastAPI의 기존 `RoutineResult`와 같은 구조입니다. 운동 카드는 `data.days[].exercises[]`로 렌더링합니다.

```json
{
  "status": 200,
  "code": "TRAINER_ROUTINE_RECOMMENDATION_200",
  "message": "수강생 맞춤 루틴 추천이 완료되었습니다.",
  "data": {
    "status": "COMPLETE",
    "title": "주 3회 전신 루틴",
    "summary": "최근 운동 기록을 반영한 루틴입니다.",
    "days": [],
    "cautions": [],
    "missingData": [],
    "sources": []
  }
}
```

`data.status`는 `COMPLETE`, `LIMITED`, `BLOCKED` 중 하나입니다. 운동일지가 없으면 `LIMITED`와 `missingData: ["workout_diaries"]`를 반환할 수 있습니다.

### 실패 코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| 400 | Validation error | 필수 값 누락·범위·enum 값 오류 |
| 401 | UNAUTHORIZED | 인증 실패 |
| 403 | `TRAINER_MEMBER_ACCESS_DENIED` | 담당 중인 활성 PT 수강생이 아님 |
| 502 | `AI_SERVICE_UNAVAILABLE` | FastAPI/AI 서비스 호출 실패 |
