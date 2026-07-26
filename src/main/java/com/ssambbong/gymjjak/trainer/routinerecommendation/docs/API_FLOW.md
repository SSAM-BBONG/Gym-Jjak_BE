# 트레이너 수강생 루틴 추천 흐름

```text
Frontend
  → POST /api/trainers/members/{memberId}/routine-recommendations
  → Spring Security: TRAINER 권한 확인
  → TrainerRoutineRecommendationController
  → TrainerRoutineRecommendationService
      → 활성 PT 담당 관계 확인 (trainerUserId, memberId)
      → 최근 28일 수강생 운동일지·세트 조회
      → 폼 입력값 + 운동일지 스냅샷 구성
  → TrainerRoutineFastApiClientAdapter
      → POST FastAPI /api/v1/routines/trainer-analysis
  → FastAPI: RAG/LLM으로 RoutineResult 생성
  → Spring GlobalApiResponse로 프론트 반환
```

FastAPI는 사용자 JWT로 Spring API를 재호출하지 않습니다. Spring이 권한과 담당 관계를 검증한 뒤 요청 범위의 최소 데이터만 전달합니다.
