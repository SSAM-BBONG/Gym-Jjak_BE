# 🔄 트레이너 메인 페이지 대시보드 Flow

## 1. 전체 조회 흐름

```text
TRAINER Access Token
→ SecurityConfig: GET /api/dashboard/trainer/main, TRAINER 권한 확인
→ TrainerMainPageController
→ AuthUser.userId 추출
→ TrainerMainPageQueryService.findMainPage(userId)
→ TrainerProfileRepository.findByUserId(userId)
→ ACTIVE 트레이너 프로필 확인
→ TrainerMainOrganizationQueryPort.countActiveOrganizations(trainerProfileId)
→ TrainerMainPtQueryPort.countCurrentStudents(trainerProfileId)
→ TrainerMainPtQueryPort.findTopCoursesByCurrentStudentCount(trainerProfileId, 4)
→ organizationId 목록 중복 제거 후 조직명 배치 조회
→ thumbnailFileId 목록 중복 제거 후 파일 URL 배치 조회
→ TrainerMainPageResponse
→ GlobalApiResponse<TrainerMainPageResponse>
```

## 2. 트레이너 프로필 조회

```text
userId
→ TrainerProfileRepository.findByUserId(userId)
→ TrainerProfile 존재 여부 확인
→ status == ACTIVE 확인
→ trainerProfileId, trainerName, averageRating, reviewCount 사용
```

- 트레이너 프로필이 없거나 `ACTIVE`가 아니면 `TRAINER_PROFILE_404_1`을 반환합니다.
- 평균 만족도와 리뷰 수는 강사평 테이블을 매번 재집계하지 않고 `TrainerProfile`에 저장된 값을 사용합니다. ⚡

## 3. 소속 조직 수·조직명 조회

```text
trainerProfileId
→ TrainerMainOrganizationQueryPort
→ SpringDataOrganizationTrainerRepository
→ organization_trainers
   WHERE trainer_profile_id = :trainerProfileId
     AND removed_at IS NULL
→ COUNT(DISTINCT organization_id)

상위 PT 카드의 organizationId 목록
→ distinct()
→ SpringDataOrganizationRepository.findAllById(organizationIds)
→ { organizationId : businessName } Map 반환
```

- 소속 해제된 관계는 제외합니다.
- 카드마다 조직명을 단건 조회하지 않아 N+1을 방지합니다.
- 강습의 `organizationId`가 없으면 빈 Map에 `null` 키를 조회하지 않고 카드의 조직명을 `null`로 반환합니다.

## 4. 현재 수강생·상위 PT 카드 조회

```text
trainerProfileId
→ TrainerMainPtQueryPort
→ SpringDataPtCourseRepository
→ pt_courses LEFT JOIN pt_reservations
→ VISIBLE, HIDDEN 강습만 선택
→ soft delete, BLOCKED, DELETED 강습 제외

전체 수강생
→ COUNT(DISTINCT pt_course_id, user_id)

상위 카드
→ 강습별 COUNT(DISTINCT CASE WHEN pr.status = 'IN_PROGRESS' THEN user_id END)
→ currentStudentCount DESC, ptCourseId DESC
→ LIMIT 4
```

- 동일 사용자가 서로 다른 강습을 수강하면 강습별로 각각 합산합니다.
- `LEFT JOIN`으로 예약이 없는 내 활성 강습도 카드 후보에 포함하며, 수강생 수는 `0`으로 반환합니다.
- 수강생 수 내림차순 정렬로 수강생이 있는 강습이 우선 노출되고, 남은 자리에 0명 강습이 노출됩니다.
- 기존 일반 사용자용 `/api/pt-courses/popular` 조회 정책은 변경하지 않습니다.

## 5. 썸네일 URL 변환

```text
상위 카드의 thumbnailFileId 목록
→ null 제거 + distinct()
→ FileUrlUseCase.getUrls(fileIds, null, false)
→ { fileId : FileUrlResult } Map 반환
→ 카드별 thumbnailFileId로 URL 연결
```

- 카드가 없거나 썸네일 ID가 없으면 파일 URL 조회를 호출하지 않습니다.
- 파일 URL 배치 조회가 실패해도 전체 API는 실패시키지 않고 해당 카드의 `thumbnailUrl`을 `null`로 반환합니다.

---

## 6. 공개 트레이너 프로필 상세 조회 Flow

```text
비로그인 또는 로그인 사용자
→ SecurityConfig: GET /api/trainers/{trainerProfileId} permitAll
→ TrainerProfileController.getTrainerProfileDetail(trainerProfileId)
→ @Positive로 1 이상 ID 검증
→ TrainerProfileQueryService.getTrainerProfileDetail(trainerProfileId)
→ TrainerProfileRepository.findById(trainerProfileId)
→ TrainerCertificationRepository.findAllByTrainerProfileId(...)
→ TrainerAwardRepository.findAllByTrainerProfileId(...)
→ FileUrlUseCase.getUrl(profileFileId, null, false)
→ TrainerProfileDetailResponse
```

- 공개 상세에는 프로필 이미지 URL과 자격증의 이름·유형만 반환합니다.
- 자격증 파일 URL과 원본 파일명은 반환하지 않습니다. 🔒

## 7. 내 트레이너 프로필 상세 조회 Flow

```text
TRAINER Access Token
→ SecurityConfig + @PreAuthorize: TRAINER 확인
→ TrainerProfileController.getMyTrainerProfile()
→ AuthUser.userId 추출
→ TrainerProfileQueryService.getMyTrainerProfile(userId)
→ TrainerProfileRepository.findByUserId(userId)
→ 자격증·수상 경력 조회
→ FileUrlUseCase.getUrl(profileFileId, userId, false)
→ 각 자격증 파일 URL 조회
→ MyTrainerProfileResponse
```

- 본인 프로필 조회에서는 프로필 이미지와 자격증 파일의 URL·원본 파일명을 반환합니다.
- 필수 파일을 찾을 수 없는 경우 해당 파일 필드는 `null`로 처리합니다.

## 8. 내 트레이너 프로필 수정 Flow

```text
TRAINER Access Token
→ SecurityConfig + @PreAuthorize: TRAINER 확인
→ TrainerProfileController.updateMyTrainerProfile()
→ UpdateTrainerProfileCommand 생성
→ TrainerProfileCommandService
→ 이미지 수정 규칙 검증
→ 현재 TrainerProfile 조회
→ REPLACE이면 새 PROFILE_IMAGE 파일 등록
→ TransactionTemplate 내부에서 프로필·추가 자격증·수상 경력 수정
→ DB 수정 실패 시 새 파일 정리
→ 성공 후 REPLACE/DELETE의 기존 이미지 파일 정리
→ UpdateTrainerProfileResponse
```

- `additionalCertifications`, `awardHistories`, `introduction`이 `null`이면 기존 값을 유지합니다.
- 자격증과 수상 경력은 값이 전달되면 기존 목록을 삭제한 후 전체 교체합니다.

## 9. 트레이너 검색 Flow

```text
ORGANIZATION 또는 ADMIN Access Token
→ SecurityConfig + @PreAuthorize: ORGANIZATION, ADMIN 확인
→ TrainerProfileController.searchTrainers()
→ keyword 정규화: null/공백은 null, 그 외 trim
→ page 기본값 0, size 기본값 10
→ TrainerProfileQueryService.searchTrainers(condition)
→ TrainerProfileSearchQueryPort
→ SpringDataTrainerProfileRepository.searchTrainers(...)
→ ACTIVE 상태 + username/name/nickname 접두어 검색
→ Slice 결과를 SearchTrainerListResponse로 변환
```

- 검색어가 없으면 ACTIVE 트레이너 전체를 조회합니다.
- `Slice`를 사용해 다음 페이지 존재 여부만 반환합니다.

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - 트레이너 메인 대시보드의 Controller → Application → Port → Repository 흐름을 정리했습니다. 🧭
  - 조직명·썸네일 배치 조회와 PT 수강생 집계 정책을 기록했습니다. ⚡
  - 기존 트레이너 프로필 조회·수정·검색 API의 계층별 흐름을 추가했습니다. 📚
  - 수강생이 없는 내 활성 PT 강습까지 카드 후보로 포함하는 흐름을 반영했습니다. 🪪
  - 소속 조직 ID가 없는 강습 카드의 NPE 방어 흐름을 추가했습니다. 🛡️
