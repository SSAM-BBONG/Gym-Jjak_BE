# 🏢 조직 API Flow

> 이 문서는 조직 도메인의 주요 API 내부 흐름을 설명합니다.

---

## 1. 책임 범위

| 구성요소 | 책임 |
| --- | --- |
| `OrganizationController` | 요청값 검증, 인증 사용자 ID/역할 추출, UseCase 호출 |
| `OrganizationCommandService` | 조직 정보 수정 쓰기 흐름 |
| `OrganizationQueryService` | 목록·상세·검색 조회 흐름 |
| `OrganizationRepository` | 조직 엔티티 조회 및 수정 |
| `OrganizationTrainerRepository` | 조직 소속 트레이너 조회 (상세 조회에서 트레이너 목록 포함) |
| `FileUrlUseCase` | 사업자등록증 파일 URL 조회 |

---

## 2. 내 조직 정보 수정 (`PATCH /api/organizations/me`)

```text
조직 계정
  → OrganizationController
  → OrganizationUpdateCommand (organizationAccountId, facilityPhone, instagramUrl, blogUrl, websiteUrl)
  → OrganizationCommandService
  → OrganizationRepository.findByOrganizationAccountId(organizationAccountId)
      └─ 없으면 ORG_ORG_404
  → Organization.update(facilityPhone, instagramUrl, blogUrl, websiteUrl)
  → OrganizationRepository.update(updated)
  → [커밋 이후] 메트릭 기록
  → 200 응답
```

### 단계별 설명

1. ORGANIZATION 권한 계정만 호출할 수 있습니다.
2. 조직 계정 ID(`organizationAccountId`)로 조직을 조회합니다. 존재하지 않으면 `ORG_ORG_404`를 반환합니다.
3. 도메인의 `update()` 메서드로 수정 가능한 필드(시설 연락처·SNS 링크)만 변경합니다.
4. 상호명, 대표자명, 사업자등록번호 등 기본 정보는 이 API에서 변경할 수 없습니다.
5. 트랜잭션 커밋 이후 메트릭을 기록합니다.

---

## 3. 조직 목록 조회 — 관리자 (`GET /api/organizations`)

```text
관리자
  → OrganizationController
  → OrganizationListQuery (page, size, keyword)
  → OrganizationQueryService
  → OrganizationRepository.findAllForAdmin(query)
  → 200 응답
```

### 단계별 설명

1. ADMIN 권한 계정만 호출할 수 있습니다.
2. `keyword`가 있으면 상호명 또는 대표자명 LIKE 검색을 수행하고, 없으면 전체 목록을 반환합니다.
3. 목록 항목에는 트레이너 수(`trainerCount`)와 조직 상태(`status`)가 포함됩니다.

---

## 4. 조직 상세 조회 — 관리자 (`GET /api/organizations/{organizationId}`)

```text
관리자
  → OrganizationController
  → OrganizationQueryService.findOrganizationAdminDetail(organizationId)
  → OrganizationRepository.findById(organizationId)         ← 없으면 ORG_ORG_404
  → OrganizationRepository.findRequestedLoginIdById(...)    ← 신청 당시 로그인 ID 조회
  → OrganizationTrainerRepository.findAdminTrainersByOrganizationId(...)
  → OrganizationAdminDetailResult 조립
  → FileUrlUseCase.getUrl(businessLicenseFileId, userId, isAdmin=true)
  → 200 응답
```

### 단계별 설명

1. 조직 기본 정보와 신청 당시 로그인 ID를 별도로 조회하여 조합합니다.
   - 로그인 ID는 `Organization` 도메인이 아닌 `organizationApplication` 테이블에서 조회합니다.
   - 신청 내역이 없을 경우 로그인 ID는 `null`로 반환됩니다.
2. 소속 트레이너 목록과 수를 함께 응답에 포함합니다.
3. 사업자등록증 파일은 ADMIN 권한으로 `FileUrlUseCase`를 통해 URL을 조회합니다.

---

## 5. 조직 상세 조회 — 사용자 (`GET /api/organizations/{organizationId}/detail`)

```text
(인증 불필요)
  → OrganizationController
  → OrganizationQueryService.findOrganizationDetail(organizationId)
  → OrganizationRepository.findById(organizationId)                             ← 없으면 ORG_ORG_404
  → OrganizationTrainerRepository.findTrainerDetailsByOrganizationId(...)       ← 트레이너별 평점·리뷰 수
  → OrganizationTrainerRepository.countAccumulatedMembersByOrganizationId(...)  ← 누적 회원 수
  → 평균 평점 계산 (Java: trainers stream 평균)
  → 200 응답
```

### 단계별 설명

1. 인증 없이 접근 가능한 공개 정보만 반환합니다. 사업자등록증 등 민감 정보는 포함되지 않습니다.
2. 소속 트레이너가 없으면 `avgRating`은 `0.0`으로 반환됩니다.
3. `accumulatedMembers`는 각 트레이너의 누적 PT 수강 회원 수의 합산입니다.
4. 평균 평점은 DB 집계가 아닌 애플리케이션 레이어에서 계산됩니다.

---

## 6. 내 조직 정보 조회 (`GET /api/organizations/me`)

```text
조직 계정
  → OrganizationController
  → OrganizationQueryService.findMyOrganization(organizationAccountId)
  → OrganizationRepository.findMyOrganizationByAccountId(organizationAccountId)
      └─ 없으면 ORG_ORG_404
  → MyOrganizationResult 반환
  → FileUrlUseCase.getUrl(businessLicenseFileId, organizationAccountId, isOrganization=true)
  → 200 응답
```

### 단계별 설명

1. ORGANIZATION 권한 계정이 본인 조직만 조회할 수 있습니다.
2. 사업자등록증은 신청자(`USER` 계정)가 업로드했지만, 조직 계정도 소유 조직 기준으로 조회할 수 있도록 `isOrganization=true`를 전달해 소유권 체크를 우회합니다. (파일 ID 자체는 DB에서 조직 소유 기준으로 안전하게 조회됩니다.)

---

## 7. 조직 검색 (`GET /api/organizations/search`)

```text
사용자 또는 트레이너
  → OrganizationController
  → OrganizationSearchQuery (keyword, page, size)
  → OrganizationQueryService.searchOrganizations(query)
  → keyword가 null 또는 공백이면 빈 목록 즉시 반환 (DB 호출 없음)
  → OrganizationRepository.searchOrganizations(query)   ← 상호명/대표자명 LIKE 검색, ACTIVE 조직만
  → 200 응답
```

### 단계별 설명

1. USER 또는 TRAINER 권한 계정만 호출할 수 있습니다. (소속 신청 시 조직 탐색 목적)
2. `keyword`가 없거나 공백이면 DB 조회 없이 바로 빈 목록을 반환합니다.
3. 검색 대상은 활성(`ACTIVE`) 조직만이며, 상호명 또는 대표자명을 기준으로 부분 일치 검색합니다.
4. 결과에는 `organizationId`, `businessName`, `representativeName`, `roadAddress`, `detailAddress`만 포함됩니다. (소속 신청 화면에서 조직을 선택하기 위한 최소 정보)

---

## 📝 문서 정보

- 작성일: `2026-07-21`
