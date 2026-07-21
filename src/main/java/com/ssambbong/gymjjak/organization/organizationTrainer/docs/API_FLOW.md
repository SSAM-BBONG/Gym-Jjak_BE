# 👥 조직 소속 트레이너 API Flow

> 이 문서는 조직 소속 트레이너 도메인의 주요 API 내부 흐름을 설명합니다.

---

## 1. 책임 범위

| 구성요소 | 책임 |
| --- | --- |
| `OrganizationTrainerController` | 조직 계정 기준 소속 트레이너 조회·추가·삭제 |
| `TrainerOrganizationController` | 트레이너 계정 기준 소속 조직 목록 조회 |
| `OrganizationTrainerCommandService` | 트레이너 추가·삭제 쓰기 흐름 |
| `OrganizationTrainerQueryService` | 트레이너 목록·소속 조직 조회 흐름 |
| `OrganizationRepository` | 조직 계정 ID로 조직 조회 (addTrainer, removeTrainer 시 조직 확인 목적) |
| `OrganizationTrainerRepository` | 소속 트레이너 관계 저장·조회·삭제 |
| `TrainerProfileQueryPort` | 트레이너 계정의 활성 트레이너 프로필 ID 조회 |

---

## 2. 소속 트레이너 추가 (`POST /api/organizations/me/trainers`)

```text
조직 계정
  → OrganizationTrainerController
  → AddOrganizationTrainerCommand (organizationAccountId, trainerProfileId)
  → OrganizationTrainerCommandService
  → OrganizationRepository.findByOrganizationAccountId(organizationAccountId)
      └─ 없으면 ORG_ORG_404
  → OrganizationTrainerRepository.existsActiveByOrganizationIdAndTrainerProfileId(organizationId, trainerProfileId)
      └─ 존재하면 ORG_OT_409
  → OrganizationTrainer.create(organizationId, trainerProfileId, organizationAccountId)
  → OrganizationTrainerRepository.save(organizationTrainer)
      └─ uk_active_organization_trainer 제약 위반 시 ORG_OT_409 (동시 요청 방어)
  → 메트릭 기록 (트랜잭션 밖, 실패해도 무시)
  → 201 응답 (organizationTrainerId)
```

### 단계별 설명

1. ORGANIZATION 권한 계정만 호출할 수 있습니다.
2. `organizationAccountId`로 조직을 먼저 조회합니다. 연결된 조직이 없으면 `ORG_ORG_404`를 반환합니다.
3. 동일 트레이너가 이미 소속 중인지 사전 체크합니다. 이미 있으면 `ORG_OT_409`를 반환합니다.
4. DB 유니크 제약(`uk_active_organization_trainer`)으로 동시 요청에 의한 중복 소속도 방어합니다. 제약 위반이 발생하면 마찬가지로 `ORG_OT_409`를 반환합니다.
5. 메트릭 기록은 트랜잭션 외부에서 실행되며, 실패해도 응답에 영향을 주지 않습니다.

---

## 3. 소속 트레이너 삭제 (`DELETE /api/organizations/me/trainers/{organizationTrainerId}`)

```text
조직 계정
  → OrganizationTrainerController
  → OrganizationTrainerCommandService.removeTrainer(organizationAccountId, organizationTrainerId)
  → OrganizationRepository.findByOrganizationAccountId(organizationAccountId)
      └─ 없으면 ORG_ORG_404
  → OrganizationTrainerRepository.findActiveByIdAndOrganizationId(organizationTrainerId, organizationId)
      └─ 없으면 ORG_OT_404  ← 소유권 검증이 조회 단계에 내포됨
  → OrganizationTrainerRepository.remove(organizationTrainerId)
  → 메트릭 기록 (실패해도 무시)
  → 200 응답
```

### 단계별 설명

1. ORGANIZATION 권한 계정만 호출할 수 있습니다.
2. `findActiveByIdAndOrganizationId`를 통해 조직 ID와 소속 트레이너 ID를 동시에 검증합니다. 본인 조직 소속이 아니거나 이미 삭제된 경우 `ORG_OT_404`를 반환합니다.
3. 삭제는 소프트 딜리트(`removedAt` 설정) 방식입니다.

---

## 4. 내 조직 소속 트레이너 목록 조회 (`GET /api/organizations/me/trainers`)

```text
조직 계정
  → OrganizationTrainerController
  → OrganizationTrainerQueryService.findMyOrganizationTrainers(organizationAccountId)
  → OrganizationRepository.findByOrganizationAccountId(organizationAccountId)
      └─ 없으면 ORG_ORG_404
  → OrganizationTrainerRepository.findTrainersByOrganizationId(organizationId)
  → 200 응답 (trainers 목록)
```

### 단계별 설명

1. ORGANIZATION 권한 계정만 호출할 수 있습니다.
2. `removedAt IS NULL` 조건으로 현재 활성 소속 트레이너만 조회합니다.
3. 응답에는 트레이너 계정 정보(`username`, `nickname`)와 프로필 정보(`trainerName`)가 함께 포함됩니다.

---

## 5. 내 소속 조직 목록 조회 — 트레이너 (`GET /api/organizations/trainer/my-organizations`)

```text
트레이너 계정
  → TrainerOrganizationController
  → OrganizationTrainerQueryService.findMyOrganizations(userId)
  → TrainerProfileQueryPort.findActiveTrainerProfileIdByUserId(userId)   ← 활성 트레이너 프로필 ID 조회
  → SpringDataOrganizationTrainerRepository.findAllByTrainerProfileIdAndRemovedAtIsNull(trainerProfileId)
  → organizationId 목록 추출
  → organizationId가 비어있으면 빈 목록 반환
  → SpringDataOrganizationRepository.findAllById(orgIds)
  → MyOrganizationResult 목록 변환
  → 200 응답
```

### 단계별 설명

1. TRAINER 권한 계정만 호출할 수 있습니다.
2. `TrainerProfileQueryPort`로 활성 트레이너 프로필 ID를 먼저 확인합니다. 프로필이 없으면 예외가 발생합니다.
3. 소속된 조직이 없으면 빈 배열을 반환합니다. (404가 아닌 200 + 빈 배열)
4. 소속 트레이너 테이블 → 조직 ID 목록 추출 → 조직 조회 순서로 2-step 조회가 이루어집니다.

---

## 6. 소속 트레이너 상태 관리

```text
OrganizationTrainer 도메인 객체
  ├─ create()      → removedAt = null (활성)
  └─ remove()      → removedAt = 현재 시간 (소프트 딜리트)
```

| 조회 조건 | 대상 |
| --- | --- |
| `removedAt IS NULL` | 현재 소속 중인 트레이너 |
| 제약: `uk_active_organization_trainer` | `organizationId + trainerProfileId` 조합 유니크 (활성 상태에서만 적용) |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
