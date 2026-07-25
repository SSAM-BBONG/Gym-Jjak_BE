# Chatbot Personal Data Payload Benchmark

## 목적

챗봇 루틴 추천 요청에서 최근 28일 운동일지를 전부 상세 데이터로 전송하던 방식을,
최근 상세 운동 최대 30건과 28일 전체 요약으로 분리한 뒤 요청 본문 크기와 Spring JSON 직렬화 비용을 비교한다.

## 측정 조건

- 측정 코드: `ChatbotPersonalDataPayloadBenchmarkTest`
- 입력: 최근 28일 안의 운동 종목 120건, 각 종목당 3세트(총 360세트)
- 상세 운동 제한: 최근 30건
- 워밍업: 200회
- 측정: 1,000회 반복 평균
- 측정 범위: Spring JVM 안에서의 DTO 생성 및 JSON 직렬화

> DB 조회, Spring-FastAPI 네트워크 전송, FastAPI 처리, LLM 응답 시간은 이 측정에 포함하지 않는다.

## 결과

| 항목 | 전체 상세 전송 | 요약 전송 |
| --- | ---: | ---: |
| 전송 본문 크기 | 23,015 B | 6,101 B |
| JSON 직렬화 평균 | 126 μs | 33 μs |

- 본문 크기 감소: 약 73.5%
- JSON 직렬화 시간 감소: 약 73.8%
- 28일 요약 생성 평균 시간: 67 μs

## 전송 구조

```json
{
  "personal_data": {
    "onboarding": {},
    "recent_workouts": [],
    "workout_summary": {
      "period_days": 28,
      "workout_days": 0,
      "part_session_counts": {},
      "part_total_volume_kg": {}
    },
    "inbodies": []
  }
}
```

`recent_workouts`는 최신 운동 종목 최대 30건의 세트 상세이고, `workout_summary`는 28일 전체의 운동 일수·부위별 세션 수·부위별 총 볼륨을 보존한다.
