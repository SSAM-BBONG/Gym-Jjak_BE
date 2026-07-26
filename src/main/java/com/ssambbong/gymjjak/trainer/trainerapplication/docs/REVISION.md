# 🔄 트레이너 신청 다중 조직 선택 개선

## ✅ 2026-07-22 · 필수 자격증 OCR 템플릿 미매칭 처리

### 오류 처리 개선

- Clova OCR 응답의 `message`가 `NOT_FOUND: not found matched template`이거나
  `validationResult.result`가 `NO_REQUESTED`이면, 외부 OCR 장애가 아닌 자격증
  템플릿 미매칭으로 처리합니다.
- 템플릿 미매칭은 빈 OCR 결과로 전달하고, 기존 필수 자격증 검증에서
  `400 Bad Request` / `TRAINER_APPLICATION_400_2`를 반환합니다.
- 사용자에게는 `필수 자격증을 확인할 수 없습니다. 올바른 자격증 이미지를 업로드해 주세요.`를 안내합니다.
- Clova 5xx·네트워크·타임아웃만 기존 재시도 정책을 적용하며, 템플릿 미매칭은 재시도하지 않습니다.

### 실패 보상 처리

- OCR 검증 실패 시, 요청에서 등록한 프로필 이미지와 자격증 파일을 모두 삭제합니다.
- S3 객체 삭제 후 `files` 테이블의 파일 메타데이터도 함께 삭제합니다.
- OCR 검증 실패 시 `trainer_applications` 행은 생성하지 않습니다.

### 검증

- Clova 템플릿 미매칭 응답 fixture(`NOT_FOUND`, `NO_REQUESTED`) 회귀 테스트를 추가했습니다.
- Spring Retry가 비재시도 `OcrException`을 `ExhaustedRetryException`으로 변환하지 않는 회귀 테스트를 추가했습니다.
- 실제 프론트 트레이너 신청에서 일반 PNG 업로드 시 `400 / TRAINER_APPLICATION_400_2` 응답과 프로필·자격증 파일 보상 삭제를 확인했습니다.

## ✅ 2026-07-18 · 사용자 트레이너 신청 목록·상세 조회 완료

### API 경로 변경

| 구분 | 기존 | 변경 |
| --- | --- | --- |
| 사용자 조회 | `GET /api/trainer-applications/me`에서 최신 1건 상세 조회 | `GET /api/trainer-applications/me?page=0`에서 신청 목록 조회 |
| 사용자 상세 | 없음 | `GET /api/trainer-applications/me/{trainerApplicationId}` |

### 구현 내용

- ✨ `MyTrainerApplicationSummaryResult`와 `MyTrainerApplicationListResult`를 추가해 사용자 목록용 조회 모델을 분리했습니다.
- 🏋️ 목록 조회는 `trainer_applications`와 `organizations`를 조인해 조직명을 단일 쿼리로 가져옵니다.
- 📄 목록 항목은 신청 ID, 헬스장명, 상태, 신청일, 심사일, 반려 사유만 반환합니다.
- 🔢 페이지 번호는 `page`로 받고, 페이지 크기는 서비스 정책에 따라 `10`건으로 고정했습니다.
- 🔒 상세 조회는 `userId + trainerApplicationId` 조건을 함께 사용해 본인 소유 신청서만 반환합니다.
- 🧩 목록·상세 응답을 각각 `MyTrainerApplicationListResponse`, `MyTrainerApplicationSummaryResponse`, 기존 상세 Response로 분리했습니다.
- 📚 Controller Swagger 설명을 실제 목록·상세 엔드포인트에 맞춰 갱신했습니다.

### 검증

- ✅ 목록 조회 Contract 및 페이지 크기 정책 단위 테스트를 추가했습니다.
- ✅ `./gradlew.bat test --tests "*TrainerApplication*"` 테스트를 통과했습니다.
- ✅ 목록/상세 조회 Postman 테스트를 통과했습니다.

> 관련 사용자 관점 변경 이력은 [CHANGELOG.md](CHANGELOG.md)에서 확인할 수 있습니다. 🎉

---

> 작성일: 2026-07-17  
> 상태: 🚧 다중 조직 신청 생성 완료 · 내 신청 목록 조회 설계 예정

## 🎯 변경 목적

트레이너 신청 시 사용자가 여러 조직을 선택할 수 있도록 지원한다. 선택한 조직마다 독립적인 신청 건을 생성하므로, 각 조직은 자신의 신청 건만 승인 또는 반려할 수 있다.

## ✅ 확정된 정책

- 요청의 `organizationId`를 `organizationIds: List<Long>`로 변경한다.
- 선택한 조직 ID마다 `trainer_applications` 행을 하나씩 생성한다.
- 각 신청 건은 독립적인 `PENDING`, `APPROVED`, `REJECTED` 상태를 가진다.
- 한 조직의 승인 또는 반려는 다른 조직의 신청 건 상태에 영향을 주지 않는다.
- 최초 승인만 사용자 역할을 `TRAINER`로 전환하고 트레이너 프로필을 생성한다.
- 이후 다른 조직의 승인에서는 기존 트레이너 프로필을 재사용하고, 해당 조직의 소속 정보만 추가한다.

## 🧭 처리 흐름

```text
Controller
  → CreateTrainerApplicationRequest.organizationIds
  → CreateTrainerApplicationCommand.organizationIds
  → TrainerApplicationCommandService
      1. 요청 목록 검증
      2. 모든 대상 조직 활성 상태 검증
      3. 파일 등록 및 OCR 검증 1회 수행
      4. organizationIds별 TrainerApplication 생성
      5. saveAll(...)로 한 트랜잭션 안에서 저장
  → TrainerApplicationRepository / Persistence Adapter
```

## 🛡️ 검증 및 예외 처리

- `organizationIds`는 null 또는 빈 목록일 수 없다.
- 목록에는 null, 0 이하 ID, 중복 ID를 포함할 수 없다.
- 대상 조직 중 하나라도 비활성 또는 존재하지 않으면 전체 요청을 실패시킨다.
- 조직 검증이 완료되기 전에는 파일 등록, OCR, 신청 저장을 실행하지 않는다.
- 기존 `uk_trainer_applications_duplicate_blocking_user` 제약은 사용자당 `PENDING`/`APPROVED` 신청 한 건만 허용하므로, 다중 행 생성 전에 제거 또는 정책에 맞게 교체해야 한다.
- 중복 신청 정책은 서비스에서 사용자 기준 `PENDING` 신청 존재 여부를 확인해 유지한다.
- 동시 승인 상황에서도 사용자당 트레이너 프로필은 한 번만 생성되어야 한다.

## 🧩 영향 범위

| 계층 | 변경 내용 |
| --- | --- |
| Presentation | 요청 DTO 및 Controller의 조직 ID 전달 타입 변경 |
| Application | Command 검증, 다중 조직 검증, 다건 저장 처리 |
| Domain | 조직별 독립 신청 상태 모델 유지 |
| Persistence | 다건 저장 지원 및 기존 조직별 조회 인덱스 활용 |
| Migration | 기존 사용자 단건 신청 유니크 제약을 다중 조직 신청 정책에 맞게 변경 |
| Approval | 최초 승인/후속 승인 분기 및 프로필 중복 생성 방지 |
| Test | 다중 생성, 유효성 실패, 독립 심사, 프로필 단일 생성 검증 |

## 🧪 완료 기준

- [ ] 조직 3개를 선택하면 신청 행 3개가 생성된다.
- [ ] 파일 등록과 OCR 검증은 요청당 한 번만 수행된다.
- [ ] 각 조직은 자신에게 속한 신청 건만 심사할 수 있다.
- [ ] 한 조직의 승인·반려가 다른 조직의 `PENDING` 상태를 변경하지 않는다.
- [ ] 여러 조직의 승인에도 트레이너 프로필은 중복 생성되지 않는다.
- [ ] 단위 테스트와 관련 통합 테스트가 통과한다.

## 📌 후속 문서

구현 및 검증이 완료되면 같은 디렉터리에 `CHANGELOG.md`를 추가한다. 해당 문서에는 사용자 영향 중심의 변경 요약과 본 문서 링크를 기록한다.

## ✅ 완료: 다중 조직 신청 생성 API

- `organizationId` 요청 값을 `organizationIds` 목록으로 변경했다.
- 대상 조직 수만큼 `trainer_applications` 행을 생성하고, 생성된 신청 ID 목록을 응답한다.
- 파일 등록과 OCR 검증은 요청당 한 번만 수행한다.
- `user_id + organization_id` 기준으로 진행 중(`PENDING`, `APPROVED`) 중복 신청을 차단하도록 DB 유니크 키를 변경했다.
- 단건 수정 응답은 `UpdateTrainerApplicationResponse`로 분리해 생성 응답과 책임을 구분했다.
- 조직 활성 여부는 조직 ID별 반복 조회 대신, `IN` 기반 단일 count 조회로 검증한다.
- V3.3 유니크 인덱스명 변경에 맞춰 중복 신청 예외 변환 조건을 갱신했다.

## 🔜 다음 작업: 내 신청 목록 조회 및 상세 경로 변경

### API 경로

- `GET /api/trainer-applications/me`: 내 신청서 목록 조회로 변경
- `GET /api/trainer-applications/me/{trainerApplicationId}`: 선택한 신청서 상세 조회로 변경

### 목록 응답 원칙

- 조직별 신청 행을 각각 한 건으로 반환한다.
- 목록에는 신청 ID, 조직 정보, 신청 상태, 자기소개 미리보기, 생성 시각을 포함한다.
- 수정·취소 화면이 특정 신청 건을 식별할 수 있도록 `trainerApplicationId`를 반드시 포함한다.

#### 페이징 및 목록 노출 필드 확정

- 목록 API는 `GET /api/trainer-applications/me?page=0`으로 조회하며, 페이지 크기는 10개로 고정한다.
- 목록 항목은 `trainerApplicationId`, `organizationName`, `createdAt`, `status`, `reviewedAt`, `rejectReason`을 반환한다.
- 자기소개, 자격증, 수상 이력, 파일 URL은 목록에 포함하지 않고 상세 API에서만 반환한다.

### 상세 조회 원칙

- 요청자는 자신의 신청서만 조회할 수 있다.
- 상세 응답은 기존 파일 URL, 자격증, 수상 이력, 소개, 심사 상태 정보를 유지한다.
- 다른 사용자 신청서 ID를 요청하면 기존 소유자 검증 정책에 따라 거절한다.

### 🧭 확정된 구현 계획

다중 조직 신청은 조직별로 독립된 행을 생성하므로, 사용자 조회도 단건 최신 조회가 아니라 신청 행 목록을 기준으로 처리한다.

```text
기존: GET /api/trainer-applications/me
      → 로그인 사용자의 최신 신청서 단건 상세 조회

변경: GET /api/trainer-applications/me
      → 로그인 사용자의 조직별 신청서 목록 조회

신규 경로: GET /api/trainer-applications/me/{trainerApplicationId}
          → 로그인 사용자가 소유한 특정 신청서 상세 조회
```

#### 구현 순서

1. 목록용 Query/Result/Response를 추가해 `trainerApplicationId`, `organizationId`, `organizationName`, `status`, `introductionPreview`, `createdAt`, `reviewedAt`, `rejectReason`을 반환한다.
2. 기존 `getMyTrainerApplication` 단건 상세 조회는 `trainerApplicationId`를 인자로 받도록 변경한다.
3. 상세 조회 시 신청서의 `userId`와 로그인 사용자 ID를 비교해 소유자만 조회하게 한다.
4. Controller에서 `GET /me`는 목록, `GET /me/{trainerApplicationId}`는 상세로 매핑한다.
5. 목록의 각 `trainerApplicationId`는 기존 `PATCH /{trainerApplicationId}` 및 `DELETE /{trainerApplicationId}` 호출에 사용한다.

#### 테스트 기준

- 한 사용자가 3개 조직에 신청하면 목록 API는 3개 항목을 반환한다.
- 목록은 다른 사용자의 신청을 포함하지 않는다.
- 본인 신청 ID의 상세 조회는 성공한다.
- 다른 사용자의 신청 ID 상세 조회는 `403`으로 거절한다.
