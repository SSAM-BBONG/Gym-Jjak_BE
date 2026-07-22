# Trainer Application Multi-Organization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 선택한 조직마다 독립적인 트레이너 신청 행을 만들고, 여러 조직의 승인에도 트레이너 프로필이 한 번만 생성되게 한다.

**Architecture:** 생성 요청은 `List<Long> organizationIds`를 Command까지 전달한다. 서비스는 모든 조직을 먼저 검증하고 파일/OCR을 한 번만 수행한 뒤, 조직별 신청 도메인 객체를 한 트랜잭션에서 저장한다. 승인에서는 사용자 기준 기존 프로필을 조회해 있으면 재사용하고, 없을 때만 생성한다.

**Tech Stack:** Java 21, Spring Boot, Spring Data JPA, Bean Validation, JUnit 5, Mockito, Flyway.

## Global Constraints

- 기존 Controller → Command → UseCase → Service → Port/Adapter → Repository 계층을 유지한다.
- 파일 등록과 OCR은 다중 조직 요청마다 한 번만 수행한다.
- 조직별 신청 행의 승인·반려는 서로의 상태를 변경하지 않는다.
- 문서 경로는 `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/docs/`를 유지한다.

---

### Task 1: 다중 조직 신청 입력과 Command 검증

**Files:**
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/presentation/api/request/CreateTrainerApplicationRequest.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/application/command/CreateTrainerApplicationCommand.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/presentation/api/TrainerApplicationController.java`
- Test: `src/test/java/com/ssambbong/gymjjak/trainer/trainerapplication/application/command/CreateTrainerApplicationCommandTest.java`

**Produces:** `List<Long> organizationIds()`를 제공하는 유효한 생성 Command.

- [ ] `CreateTrainerApplicationCommandTest`에 빈 목록, null/0 이하 ID, 중복 ID가 `InvalidTrainerApplicationException`을 던지는 테스트를 작성한다.
- [ ] 테스트를 실행한다.
  - Run: `./gradlew.bat test --tests "*CreateTrainerApplicationCommandTest"`
  - Expected: Command 구현 변경 전 실패.
- [ ] Request/Command의 `Long organizationId`를 `@NotEmpty List<Long> organizationIds`로 바꾸고, Command compact constructor에서 `List.copyOf`, 양수 검증, `distinct` 개수 비교로 목록을 검증한다.
- [ ] Controller가 `request.organizationIds()`를 Command에 전달하도록 수정한다.
- [ ] 같은 테스트를 다시 실행한다.
  - Expected: PASS.

### Task 2: 조직별 신청 행 일괄 생성

**Files:**
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/domain/repository/TrainerApplicationRepository.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/infrastructure/persistence/TrainerApplicationRepositoryAdapter.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/application/service/TrainerApplicationCommandService.java`
- Create: `src/main/resources/db/migration/V2.21__allow_multiple_organization_trainer_applications.sql`
- Test: `src/test/java/com/ssambbong/gymjjak/trainer/trainerapplication/application/service/TrainerApplicationCommandServiceTest.java`

**Consumes:** 검증된 `CreateTrainerApplicationCommand.organizationIds()`.

**Produces:** 선택된 조직 수와 같은 신청 행을 저장하고 첫 신청 ID를 반환하는 생성 UseCase.

- [ ] 서비스 테스트에 조직 ID 3개 요청에서 `existsActiveOrganizationById`가 각 ID에 대해 호출되고, 파일 등록/OCR은 한 번, 저장 대상은 3개인 테스트를 작성한다.
- [ ] Flyway 마이그레이션에서 `uk_trainer_applications_duplicate_blocking_user` 인덱스와 `duplicate_blocking_user_id` 생성 컬럼을 제거한다. 이 제약은 사용자당 `PENDING`/`APPROVED` 신청을 한 행만 허용하므로 다중 조직 신청과 양립하지 않는다.
- [ ] 테스트를 실행한다.
  - Run: `./gradlew.bat test --tests "*TrainerApplicationCommandServiceTest"`
  - Expected: 다건 저장 메서드 부재로 실패.
- [ ] `TrainerApplicationRepository`에 `List<TrainerApplication> saveAll(List<TrainerApplication> trainerApplications)`를 추가하고 Adapter에서 mapper 변환 후 Spring Data `saveAll`을 호출한다.
- [ ] `createTrainerApplication`에서 모든 `organizationIds`의 활성 상태를 파일/OCR 이전에 검사한다. `saveTrainerApplication`은 조직별 `TrainerApplication.create(...)` 목록을 만들고 `saveAll`로 저장한다.
- [ ] 저장 직전의 사용자 기준 `PENDING` 중복 신청 검사는 한 번만 유지한다. 실패 시 기존 파일 보상 삭제 흐름이 그대로 실행되는지 확인한다.
- [ ] 같은 테스트를 다시 실행한다.
  - Expected: PASS.

### Task 3: 최초/후속 승인에서 프로필 중복 생성 방지

**Files:**
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/application/port/out/ApprovedTrainerProfilePort.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerprofile/infrastructure/persistence/internal/ApprovedTrainerProfileAdapter.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/application/service/TrainerApplicationCommandService.java`
- Test: `src/test/java/com/ssambbong/gymjjak/trainer/trainerapplication/application/service/TrainerApplicationCommandServiceTest.java`

**Consumes:** 승인 대상 신청의 `userId`, 조직 ID, 신청 프로필 정보.

**Produces:** 기존 프로필 ID를 재사용하거나 최초 승인에서만 새 프로필 ID를 생성하는 승인 흐름.

- [ ] 서비스 테스트에 기존 프로필 ID가 있을 때 `createApprovedTrainerProfile`이 호출되지 않고 해당 ID로 조직 소속만 등록되는 테스트를 작성한다.
- [ ] 서비스 테스트에 기존 프로필이 없을 때 역할 승격과 프로필 생성이 한 번 호출되는 테스트를 작성한다.
- [ ] 테스트를 실행한다.
  - Run: `./gradlew.bat test --tests "*TrainerApplicationCommandServiceTest"`
  - Expected: 기존 프로필 조회 API 부재로 실패.
- [ ] `ApprovedTrainerProfilePort`에 `Optional<Long> findTrainerProfileIdByUserId(Long userId)`를 추가하고, `ApprovedTrainerProfileAdapter`가 `TrainerProfileRepository.findByUserId` 결과의 ID를 반환하도록 구현한다.
- [ ] 승인 서비스에서 프로필 조회 결과가 있으면 역할 승격·프로필 생성 단계를 건너뛰고, 없을 때만 기존 생성 로직을 실행하도록 분기한다. 두 경우 모두 `registerApprovedTrainer`와 신청 상태 저장은 실행한다.
- [ ] `trainer_profiles.user_id`의 기존 유니크 제약을 이용해 동시 최초 승인에서도 프로필 중복을 막는다. 유니크 충돌이 발생하면 기존 프로필을 재조회해 계속 진행하도록 Adapter 경계를 보강한다.
- [ ] 같은 테스트를 다시 실행한다.
  - Expected: PASS.

### Task 4: API 계약·회귀 검증·문서 완료

**Files:**
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/presentation/api/response/CreateTrainerApplicationResponse.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/docs/REVISION.md`
- Create: `src/main/java/com/ssambbong/gymjjak/trainer/trainerapplication/docs/CHANGELOG.md`

**Produces:** 다건 생성 결과를 명확히 전달하는 API와 완료 상태의 변경 문서.

- [ ] 생성 API의 성공 응답을 `trainerApplicationIds: List<Long>`로 변경하고, OpenAPI 예시와 응답 코드를 같은 계약으로 갱신한다.
- [ ] 단위 테스트 전체를 실행한다.
  - Run: `./gradlew.bat test --tests "*TrainerApplication*"`
  - Expected: PASS.
- [ ] 컴파일 검증을 실행한다.
  - Run: `./gradlew.bat compileJava`
  - Expected: `BUILD SUCCESSFUL`.
- [ ] `REVISION.md`의 완료 기준 체크박스를 실제 결과로 갱신한다.
- [ ] `CHANGELOG.md`에 날짜, 사용자 영향 요약, `[상세 설계](REVISION.md)` 링크를 추가한다.
- [ ] 관련 변경만 스테이징하고 기능 단위로 커밋한다.
