# 🏢 조직 신청 API Flow

> 이 문서는 조직 신청 도메인의 주요 API 내부 흐름을 설명합니다.

---

## 1. 책임 범위

| 구성요소 | 책임 |
| --- | --- |
| `OrganizationApplicationController` | 요청값 검증, 인증 사용자 ID 추출, Command/Query 전달 |
| `OrganizationApplicationCommandService` | 신청·승인·반려·취소의 쓰기 흐름 전체, 외부 Port 연계 |
| `OrganizationApplicationQueryService` | 목록·상세·중복확인 조회 |
| `OrganizationApplicationRepository` | 신청 엔티티 저장 및 조회 |
| `FileRegistrationPort` | S3에 파일 메타데이터를 등록하는 경계 |
| `UserCreationPort` | 승인 시 조직 계정을 생성하는 경계 |
| `OrganizationRepository` | 승인 시 Organization 생성·저장 |
| `ApplicationEventPublisher` | 승인·반려 이벤트 발행 |

---

## 2. 조직 신청 생성 (`POST /api/organization-applications`)

```text
사용자
  → OrganizationApplicationController
  → OrganizationApplicationCreateCommand
  → OrganizationApplicationCommandService
  → [검증 1] 사업자등록번호 형식 검증
  → [검증 2] 사업자등록번호 중복 확인 (ORG_001)
  → [검증 3] 로그인 ID 중복 확인 (ORG_002)
  → FileRegistrationPort.register(businessLicenseFile)   ← S3 파일 메타데이터 등록
      └─ 실패 시 ORG_500
  → OrganizationApplication 도메인 생성 (status = PENDING)
  → OrganizationApplicationRepository.save()
  → [커밋 이후] 메트릭 기록
  → 201 응답 (organizationApplicationId)
```

### 단계별 설명

1. 컨트롤러가 `OrganizationApplicationCreateRequest`를 검증하고 `authUser.userId()`를 함께 Command로 조립합니다.
2. 서비스가 사업자등록번호 형식을 검증합니다. 형식이 맞지 않으면 `ORG_003`으로 거부합니다.
3. 동일한 사업자등록번호의 신청이 이미 존재하면 `ORG_001`로 거부합니다.
4. 동일한 로그인 ID의 신청이 이미 존재하면 `ORG_002`로 거부합니다.
5. `FileRegistrationPort`를 통해 S3에 업로드된 파일의 메타데이터를 등록합니다. S3 등록에 실패하면 `ORG_500`으로 거부합니다.
6. 등록된 파일 ID를 포함한 `OrganizationApplication` 도메인 객체를 생성하고 저장합니다. 초기 상태는 `PENDING`입니다.
7. 트랜잭션 커밋 이후 메트릭을 기록합니다.

---

## 3. 조직 신청 승인 (`PATCH /api/organization-applications/{applicationId}/approve`)

```text
관리자
  → OrganizationApplicationController
  → OrganizationApplicationCommandService
  → OrganizationApplicationRepository.findById(applicationId)   ← 없으면 ORG_404
  → OrganizationApplication.approve()
      └─ 현재 status 확인:
         APPROVED  → ORG_409_1
         REJECTED  → ORG_409_2
         CANCELLED → ORG_409_3
         PENDING   → status = APPROVED
  → OrganizationApplicationRepository.save()
  → UserCreationPort.createOrganizationAccount(requestedLoginId, ...)  ← 조직 계정 생성
  → Organization.create(organizationApplicationId, ...)                ← 조직 정보 생성
  → OrganizationRepository.save()
  → ApplicationEventPublisher.publish(OrgApplicationApprovedEvent)
  → [커밋 이후] 메트릭 기록
  → 200 응답
```

### 단계별 설명

1. 관리자 권한(`ADMIN`)을 가진 사용자만 호출할 수 있습니다.
2. 신청이 존재하지 않으면 `ORG_404`로 거부합니다.
3. 도메인의 `approve()` 메서드에서 상태 전이를 검증합니다. `PENDING` 이외의 상태면 해당 상태에 맞는 409 에러를 반환합니다.
4. 신청 상태를 `APPROVED`로 변경하고 저장합니다.
5. `UserCreationPort`를 통해 `requestedLoginId`를 로그인 ID로 하는 조직 전용 계정을 생성합니다.
6. 신청 정보를 기반으로 `Organization` 도메인 객체를 생성하고 저장합니다.
7. `OrgApplicationApprovedEvent`를 발행하여 알림 등 후속 처리를 위임합니다.
8. 트랜잭션 커밋 이후 메트릭을 기록합니다.

---

## 4. 조직 신청 반려 (`PATCH /api/organization-applications/{applicationId}/reject`)

```text
관리자
  → OrganizationApplicationController
  → OrganizationApplicationCommandService
  → OrganizationApplicationRepository.findById(applicationId)   ← 없으면 ORG_404
  → OrganizationApplication.reject(rejectReason)
      └─ 현재 status 확인:
         APPROVED  → ORG_409_1
         REJECTED  → ORG_409_2
         CANCELLED → ORG_409_3
         PENDING   → status = REJECTED, rejectReason 저장
  → OrganizationApplicationRepository.save()
  → ApplicationEventPublisher.publish(OrgApplicationRejectedEvent)
  → [커밋 이후] 메트릭 기록
  → 200 응답
```

### 단계별 설명

1. 관리자 권한(`ADMIN`)을 가진 사용자만 호출할 수 있습니다.
2. 신청이 존재하지 않으면 `ORG_404`로 거부합니다.
3. 도메인의 `reject()` 메서드에서 상태 전이를 검증합니다.
4. 반려 사유(`rejectReason`)를 도메인에 저장하고 상태를 `REJECTED`로 변경합니다.
5. `OrgApplicationRejectedEvent`를 발행하여 신청자에게 알림이 전달되도록 위임합니다.
6. 트랜잭션 커밋 이후 메트릭을 기록합니다.

---

## 5. 조직 신청 취소 (`PATCH /api/organization-applications/{applicationId}/cancel`)

```text
신청자
  → OrganizationApplicationController
  → OrganizationApplicationCommandService
  → OrganizationApplicationRepository.findByIdAndApplicantUserId(applicationId, userId)
      └─ 없으면 ORG_404  ← 소유권 검증이 조회 단계에 내포됨
  → OrganizationApplication.cancel()
      └─ 현재 status 확인:
         APPROVED  → ORG_409_1
         REJECTED  → ORG_409_2
         CANCELLED → ORG_409_3
         PENDING   → status = CANCELLED
  → OrganizationApplicationRepository.save()
  → [커밋 이후] 메트릭 기록
  → 200 응답
```

### 단계별 설명

1. 별도의 `@PreAuthorize` 없이, 조회 쿼리 자체(`findByIdAndApplicantUserId`)에 소유권 검증이 내포되어 있습니다. 본인 신청이 아니면 `ORG_404`가 반환됩니다.
2. 도메인의 `cancel()` 메서드에서 상태 전이를 검증합니다.
3. 상태를 `CANCELLED`로 변경하고 저장합니다.
4. 트랜잭션 커밋 이후 메트릭을 기록합니다.

---

## 6. 조직 신청 상세 조회 (`GET /api/organization-applications/{applicationId}`)

```text
사용자 또는 관리자
  → OrganizationApplicationController
  → authUser.role() 확인 → isAdmin 결정
  → OrganizationApplicationQueryService.findOrganizationApplicationDetails(applicationId, userId, isAdmin)
      ├─ isAdmin = true  → findById(applicationId)         ← 모든 신청 조회 가능
      └─ isAdmin = false → findByIdAndApplicantUserId(...)  ← 본인 신청만 조회 가능 (없으면 ORG_403 또는 ORG_404)
  → FileUrlUseCase.getUrl(businessLicenseFileId, userId, isAdmin)  ← 파일 URL 조회
  → 200 응답
```

### 단계별 설명

1. ADMIN은 모든 신청의 상세를 조회할 수 있습니다. 일반 사용자는 본인이 제출한 신청만 조회할 수 있습니다.
2. 파일 URL은 `FileUrlUseCase`를 통해 S3 Presigned URL 또는 공개 URL 형태로 반환됩니다.

---

## 7. 로그인 ID 중복 확인 (`GET /api/organization-applications/login-id/duplicate`)

```text
사용자
  → OrganizationApplicationController
  → OrganizationApplicationQueryService.checkLoginIdDuplicate(requestedLoginId)
  → OrganizationApplicationRepository.existsByRequestedLoginId(requestedLoginId)
      ├─ true  → ORG_002 예외 발생
      └─ false → 200 응답 (중복 없음)
```

### 단계별 설명

1. 조직 신청 테이블에서만 중복 여부를 확인합니다. 실제 사용자 계정 테이블과는 별도입니다.
2. 이미 동일한 로그인 ID로 신청한 내역이 존재하면 `ORG_002`를 반환합니다.

---

## 8. 도메인 상태 전이

```text
         신청
           ↓
        PENDING
       /   |   \
  cancel reject approve
     ↓      ↓       ↓
CANCELLED REJECTED APPROVED
                      ↓
              조직 계정 + Organization 생성
              OrgApplicationApprovedEvent 발행
```

| 현재 상태 | 가능한 전이 | 불가한 전이 |
| --- | --- | --- |
| `PENDING` | `APPROVED`, `REJECTED`, `CANCELLED` | - |
| `APPROVED` | - | 승인·반려·취소 모두 불가 |
| `REJECTED` | - | 승인·반려·취소 모두 불가 |
| `CANCELLED` | - | 승인·반려·취소 모두 불가 |

---

## 9. 타 도메인 개발자 체크포인트 ✅

1. 승인 시 `UserCreationPort.createOrganizationAccount`를 통해 조직 계정이 자동으로 생성됩니다. 계정 생성 실패는 트랜잭션 롤백 대상입니다.
2. 승인 이벤트(`OrgApplicationApprovedEvent`)를 구독하는 리스너가 있다면, 해당 이벤트 페이로드 구조 변경 시 리스너도 함께 수정해야 합니다.
3. 로그인 ID 중복 확인은 신청 테이블 기준이므로, 이미 승인된 계정의 로그인 ID와 같더라도 현재 신청 테이블에 해당 ID의 신청이 없으면 통과합니다.

---

## 📝 문서 정보

- 작성일: `2026-07-21`
