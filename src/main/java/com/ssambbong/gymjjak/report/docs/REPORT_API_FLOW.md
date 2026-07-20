# 🚨 Report API Flow

> 이 문서는 사용자 신고 접수 API인 `POST /api/reports`의 내부 흐름을 설명합니다.  
> 관리자 심사·제재 API는 [REPORTGROUP_API_FLOW.md](REPORTGROUP_API_FLOW.md)를 참고합니다.

---

## 1. 책임 범위

`ReportController`는 신고 접수를 담당하고, `ReportGroup`은 동일 대상에 쌓인 신고를 묶어 관리합니다.

| 구성요소 | 책임 |
| --- | --- |
| `ReportController` | 요청값 검증, 인증 사용자 ID 추출, `CreateReportCommand` 전달 |
| `ReportCommandService` | 스냅샷 조회, 본인·중복 신고 검증, 그룹/개별 신고 생성, 자동 블라인드 판단 |
| `ReportTargetQueryPort` | 대상 도메인에서 신고 대상 스냅샷을 조회하는 경계 |
| `ReportGroupRepository` | 대상 타입·ID 기준 그룹 조회 및 저장 |
| `ReportRepository` | 개별 신고 중복 확인 및 저장 |
| `ReportSanctionTargetPort` | 자동 블라인드가 발생했을 때 실제 대상 도메인에 제재 요청 |

---

## 2. 신고 접수 전체 흐름

```text
사용자
  → ReportController
  → CreateReportCommand
  → ReportCommandService
  → ReportTargetQueryPort.getSnapshot(targetType, targetId)
  → 대상 도메인별 ReportTargetPort
  → ReportTargetSnapshot 반환
  → 본인 신고 검증
  → 기존 ReportGroup 조회
      ├─ 존재: 중복 신고 검증 → 신고 수 증가 → 자동 제재 상태 동기화
      └─ 없음: 스냅샷으로 새 ReportGroup 생성
  → ReportGroup 저장
  → Report 생성 및 저장
  → 자동 블라인드 조건 확인
      └─ NONE → AUTO_BLINDED 변경 시 대상 도메인에 제재 요청
  → REPORT_200_1 응답
```

### 단계별 설명

1. `POST /api/reports` 요청의 `targetId`, `targetType`, `reason`, `detail`을 검증합니다.
2. 인증 객체 `AuthUser`에서 신고자 ID를 얻어 `CreateReportCommand`를 만듭니다.
3. 신고 서비스가 `ReportTargetQueryPort`를 호출해 대상 존재 여부·소유자·스냅샷을 확인합니다.
4. 신고자와 대상 소유자가 같으면 `REPORT_400_4`로 거부합니다.
5. `(targetType, targetId)` 기준의 기존 신고 그룹을 조회합니다.
6. 기존 그룹이 있으면 같은 사용자의 신고가 있는지 검사합니다. 이미 있으면 `REPORT_409_3`입니다.
7. 그룹을 저장한 뒤, 그 그룹 ID를 가진 개별 `Report`를 생성·저장합니다.
8. 자동 블라인드 대상의 유효 신고 수가 5건 이상이 되는 순간에만 대상 도메인으로 `APPLY_AUTO_BLIND`를 전달합니다.

---

## 3. 스냅샷 조회 Port 흐름

신고 도메인은 PT 코스, 게시글, 채팅 엔티티를 직접 참조하지 않습니다. 대신 공통 모델인 `ReportTargetSnapshot`을 사용합니다.

```text
ReportCommandService
  → ReportTargetQueryPort
  → ReportTargetQueryAdapter
  → targetType switch 분기
  → 각 대상 도메인의 *ReportTargetPort
  → ReportTargetSnapshot
  → ReportGroup의 snapshot_* 컬럼 저장
```

공통 스냅샷 계약:

```java
public record ReportTargetSnapshot(
    Long targetId,
    Long targetOwnerId,
    String title,
    String content,
    String fileUrl
) {}
```

| 필드 | 신고 접수에서의 용도 | 저장 컬럼 | 현재 null 처리 |
| --- | --- | --- | --- |
| `targetId` | 신고 대상 식별 | `target_id` | 대상 Port가 반드시 반환해야 합니다. |
| `targetOwnerId` | 본인 신고 방지 | `target_owner_id` | DB는 `null` 허용이지만, 본인 신고 검증을 위해 대상 Port는 실제 소유자 ID를 반환해야 합니다. |
| `title` | 관리자 신고 목록·상세의 대상 표시 | `snapshot_title` | DB에서 `null` 허용입니다. 현재 모든 대상 타입은 실제 제목 또는 표시용 상수를 반환합니다. |
| `content` | 관리자 신고 상세에서 원문 확인 | `snapshot_content` | DB에서 `null` 허용이며, 대상 도메인 값이 그대로 저장됩니다. |
| `fileUrl` | 첨부 파일 스냅샷 | `snapshot_file_url` | DB에서 `null` 허용입니다. 현재 모든 대상 타입이 `null`을 반환합니다. |

> 💡 스냅샷은 신고 접수 당시 값을 보존하는 용도입니다. 원본 대상이 이후 수정·삭제되어도 신고 그룹에 저장된 스냅샷은 별도로 유지됩니다.

---

## 4. 대상 타입별 스냅샷 상세

### 4.1 전체 매핑 표

| `targetType` | 호출 Port | 실제 조회 대상 | `targetOwnerId` | `title` | `content` | `fileUrl` |
| --- | --- | --- | --- | --- | --- | --- |
| `PT_COURSE` | `PtCourseReportTargetPort` | `PtCourse` | `ptCourse.trainerProfileId`를 사용자 ID로 변환 | `ptCourse.title` | `ptCourse.description` | `null` |
| `FEEDBACK` | `FeedbackReportTargetPort` | `Feedback` | `feedback.trainerProfileId`를 사용자 ID로 변환 | 상수 `피드백` | `feedback.content` | `null` |
| `TRAINER_REVIEW` | `TrainerReviewReportTargetPort` | 활성 `TrainerReview` | `trainerReview.userId` | 상수 `강사평` | `trainerReview.content` | `null` |
| `POST` | `PostReportTargetPort` | 삭제되지 않은 `CommunityPostJpaEntity` | `communityPost.userId` | `communityPost.title` | `communityPost.content` | `null` |
| `COMMENT` | `CommentReportTargetPort` | 삭제되지 않은 `CommunityCommentJpaEntity` | `communityComment.userId` | 상수 `댓글` | `communityComment.content` | `null` |
| `CHAT` | `ChatReportTargetPort` | `ChatMessage` | `chatMessage.senderId` | 상수 `채팅 메시지` | `chatMessage.content` | `null` |

### 4.2 PT 코스 (`PT_COURSE`)

```text
ReportTargetQueryAdapter
  → PtCourseReportTargetPort
  → PtCourseReportTargetAdapter
  → PtCourseRepository.findById(targetId)
  → TrainerProfileQueryPort.findUserIdByTrainerProfileId(...)
  → ReportTargetSnapshot(id, ownerUserId, title, description, null)
```

- 대상이 없으면 `PtCourseNotFoundException`이 발생합니다.
- 제목과 설명을 모두 스냅샷으로 보관합니다.
- 현재 파일 URL은 저장하지 않습니다.
- `TrainerProfileQueryPort`로 트레이너 프로필 ID를 사용자 ID로 변환한 값을 `targetOwnerId`에 저장합니다. 따라서 신고자 사용자 ID와 같은 기준으로 본인 신고를 검증합니다.

### 4.3 피드백 (`FEEDBACK`)

```text
ReportTargetQueryAdapter
  → FeedbackReportTargetPort
  → FeedbackReportTargetAdapter
  → FeedbackRepository.findById(targetId)
  → TrainerProfileQueryPort.findUserIdByTrainerProfileId(...)
  → ReportTargetSnapshot(id, ownerUserId, "피드백", content, null)
```

- 피드백의 소유자는 트레이너 프로필 ID가 아니라 **변환된 사용자 ID**입니다.
- 따라서 `TrainerProfileQueryPort` 호출이 본인 신고 방지에 반드시 필요합니다.
- 제목은 원본 필드가 아닌 고정 문자열 `피드백`입니다.

### 4.4 트레이너 리뷰 (`TRAINER_REVIEW`)

```text
ReportTargetQueryAdapter
  → TrainerReviewReportTargetPort
  → TrainerReviewReportTargetAdapter
  → TrainerReviewRepository.findActiveById(targetId)
  → ReportTargetSnapshot(id, userId, "강사평", content, null)
```

- 삭제되었거나 비활성인 리뷰는 `findActiveById`에서 제외됩니다.
- 제목은 고정 문자열 `강사평`이며, 리뷰 본문이 `content`에 저장됩니다.

### 4.5 게시글 (`POST`)

```text
ReportTargetQueryAdapter
  → PostReportTargetPort
  → CommunityAdapter
  → SpringDataCommunityRepository.findByIdAndDeletedAtIsNull(targetId)
  → ReportTargetSnapshot(id, userId, title, content, null)
```

- 소프트 삭제된 게시글은 신고 대상으로 조회되지 않습니다.
- 게시글은 제목과 본문을 모두 실제 값으로 저장합니다.

### 4.6 댓글 (`COMMENT`)

```text
ReportTargetQueryAdapter
  → CommentReportTargetPort
  → CommunityCommentReportAdapter
  → SpringDataCommunityCommentRepository.findByIdAndDeletedAtIsNull(targetId)
  → ReportTargetSnapshot(id, userId, "댓글", content, null)
```

- 댓글은 독립된 제목이 없으므로 관리자 표시용 상수 `댓글`을 `title`로 저장합니다.
- 본문은 `content`에 저장하고, 소프트 삭제된 댓글은 신고 대상으로 조회되지 않습니다.

### 4.7 채팅 (`CHAT`)

```text
ReportTargetQueryAdapter
  → ChatReportTargetPort
  → ChatReportTargetAdapter
  → ChatMessageRepository.findById(targetId)
  → ReportTargetSnapshot(id, senderId, "채팅 메시지", content, null)
```

- 채팅 메시지 발신자 ID가 대상 소유자 ID입니다.
- 제목은 고정 문자열 `채팅 메시지`이며, 메시지 본문을 `content`에 저장합니다.

---

## 5. 신고 그룹 생성과 자동 블라인드

### 새 신고 그룹

해당 `(targetType, targetId)` 조합의 그룹이 없으면 스냅샷으로 `ReportGroup`을 생성합니다.

| 필드 | 초기값 |
| --- | --- |
| `totalReportCount` | `1` |
| `effectiveReportCount` | `1` |
| `reviewStatus` | `PENDING` |
| `sanctionStatus` | `NONE` |

### 기존 신고 그룹

기존 그룹이 있으면 스냅샷을 새로 저장하지 않고, 다음 값만 변경합니다.

- `totalReportCount` 증가
- `effectiveReportCount` 증가
- `reviewStatus`를 `PENDING`으로 변경
- 자동 블라인드 대상이면 유효 신고 수 기준으로 `sanctionStatus` 재계산

### 자동 블라인드

| 조건 | 결과 |
| --- | --- |
| 대상 타입이 `PT_COURSE`, `POST`, `COMMENT`이고 유효 신고 수가 5건 이상 | `AUTO_BLINDED` |
| `sanctionStatus`가 `NONE`에서 `AUTO_BLINDED`로 처음 변경 | `ReportSanctionTargetPort`에 `APPLY_AUTO_BLIND` 요청 |
| `FEEDBACK`, `TRAINER_REVIEW`, `CHAT` | 신고 접수만으로 자동 블라인드하지 않음 |

---

## 6. 타 도메인 개발자 체크포인트 ✅

1. 새 신고 대상 타입을 추가하면 `ReportTargetType`뿐 아니라 `ReportTargetQueryAdapter`의 분기와 전용 `*ReportTargetPort` 구현을 함께 추가합니다.
2. 대상 Port는 본인 신고 검증을 위해 실제 사용자 기준의 `targetOwnerId`를 반환해야 합니다.
3. 제목이 없는 대상은 댓글처럼 관리자 표시용 상수를 `title`로 제공해 목록·상세 화면의 표시를 통일합니다.
4. 첨부 파일을 신고 스냅샷에 보관해야 한다면 해당 Port에서 `fileUrl`을 반환하도록 확장해야 합니다. 현재는 모든 타입이 `null`입니다.
5. 대상이 삭제·비활성 상태일 때 신고를 허용할지 여부는 각 대상 Adapter의 조회 조건으로 결정됩니다.

---

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - PT 코스 신고 스냅샷의 소유자 ID를 `trainerProfileId`에서 사용자 ID로 변환하도록 최신화했습니다.
  - 댓글 신고 스냅샷의 제목을 `null` 대신 표시용 상수 `댓글`로 최신화했습니다.
