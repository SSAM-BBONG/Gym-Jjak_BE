# 🏋️ 트레이너 메인 페이지 대시보드 추가

## ✅ 2026-07-21 · 구현 내용

`TRAINER` 사용자가 PT 메인 페이지에서 본인의 운영 현황을 한 번에 조회할 수 있는 API를 추가했습니다.

### 제공 정보

- 🏢 활성 소속 헬스장 수
- 👥 현재 PT 수강생 수
- 📚 현재 수강생 수 기준 상위 4개 PT 강습 카드
- ⭐ 평균 만족도와 강사평 수

### 도메인 책임

```text
trainerprofile
→ 대시보드 Application Service, Result, UseCase, Controller, Response 소유

organization
→ 활성 소속 조직 수와 조직명 Query Port 구현

ptCourse
→ 현재 수강생 집계와 상위 PT 카드 Query Port 구현
```

### 집계 정책

- `organization_trainers.removed_at IS NULL`인 조직만 중복 없이 집계합니다.
- `VISIBLE`, `HIDDEN` PT 강습의 `IN_PROGRESS` 예약만 현재 수강생으로 봅니다.
- 수강생은 강습별 `DISTINCT user_id`로 집계합니다.
- 동일 사용자가 서로 다른 강습을 듣는 경우, 각 강습 수강생 수에 각각 포함합니다.
- 상위 강습은 `currentStudentCount DESC`, `ptCourseId DESC`로 정렬하며 최대 4개를 반환합니다. 수강생이 없는 활성 강습은 남은 자리에 포함합니다.
- `BLOCKED`, `DELETED`, soft delete 강습은 제외합니다.

### 성능·예외 정책 ⚡

- 카드의 조직명과 썸네일 URL은 ID 목록으로 배치 조회해 N+1을 방지합니다.
- 썸네일 파일 URL 조회 실패는 API 전체 실패가 아니라 `thumbnailUrl: null`로 처리합니다.
- 활성 트레이너 프로필이 없으면 `TRAINER_PROFILE_404_1`을 반환합니다.

### 검증

- ✅ Application 조합 서비스 테스트
- ✅ 조직 Query Adapter 테스트
- ✅ PT Query Adapter 테스트
- ✅ Controller의 200·401·403 권한 테스트

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - 트레이너 메인 페이지 대시보드 API와 도메인별 Query Port 구현을 추가했습니다. 🏋️
  - 배치 조회 기반 N+1 방지와 현재 수강생 집계 정책을 확정했습니다. ⚡
  - 수강생이 없는 내 활성 PT 강습까지 카드 목록에 노출되도록 정책을 확장했습니다. 🪪
  - 소속 조직 ID가 없는 강습 카드에서 발생 가능한 `Map.of().get(null)` NPE를 방어했습니다. 🛡️
