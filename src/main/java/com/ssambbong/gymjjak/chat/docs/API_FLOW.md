# 💬 채팅 REST API Flow

> 이 문서는 채팅 REST API의 내부 흐름을 설명합니다.
> WebSocket 메시지 전송 흐름은 [WEBSOCKET_FLOW.md](WEBSOCKET_FLOW.md)를 참고합니다.

---

## 1. 책임 범위

| 구성요소 | 책임 |
| --- | --- |
| `ChatRoomController` | 채팅방 생성·목록·나가기·안읽은수 |
| `ChatMessageController` | 메시지 목록 조회 |
| `ChatRoomService` | 채팅방 생성·나가기·목록·안읽은수 쓰기/읽기 흐름 |
| `ChatMessageService` | 메시지 조회 및 참여자 검증 |
| `ChatRoomRepository` | 채팅방 조회·저장·상태 변경 |
| `ChatMessageRepository` | 메시지 조회 |
| `TrainerQueryPort` | 트레이너 계정의 userId 조회 (참여자 검증용) |
| `FileUrlUseCase` | 트레이너 프로필 이미지 URL 일괄 조회 |

---

## 2. 채팅방 생성 (`POST /api/chat/rooms`)

```text
회원(USER)
  → ChatRoomController
  → CreateChatRoomCommand (userId, trainerProfileId, ptCourseId)
  → ChatRoomService
  → TrainerQueryPort.findActiveTrainerUserId(trainerProfileId)
      └─ 없으면 CHAT_006
  → ChatRoomRepository.existsByUserIdAndTrainerProfileIdAndPtCourseIdAndStatus(..., ACTIVE)
      └─ 존재하면 CHAT_002
  → ChatRoom.create(userId, trainerProfileId, ptCourseId)
  → ChatRoomRepository.save()
      └─ uk_chat_rooms 제약 위반 시 CHAT_002 (동시 요청 방어)
      └─ fk_chat_rooms_trainer 위반 시 CHAT_006
      └─ fk_chat_rooms_pt_course 위반 시 CHAT_007
  → [커밋 이후] 메트릭 기록
  → 201 응답 (chatRoomId)
```

### 단계별 설명

1. USER 권한 계정만 채팅방을 생성할 수 있습니다. (트레이너는 생성 불가)
2. 트레이너 존재 여부를 먼저 확인합니다.
3. 동일한 `(userId, trainerProfileId, ptCourseId, ACTIVE)` 조합의 채팅방이 이미 있으면 `CHAT_002`를 반환합니다.
4. DB 유니크 제약(`uk_chat_rooms`)으로 동시 요청 중복도 방어합니다.
5. 초기 상태는 `ACTIVE`, `userLeft = false`, `trainerLeft = false`입니다.

---

## 3. 채팅방 나가기 (`PATCH /api/chat/rooms/{chatRoomId}/leave`)

```text
회원 또는 트레이너
  → ChatRoomController
  → ChatRoomService.leaveChatRoom(chatRoomId, requesterId)
  → ChatRoomRepository.findById(chatRoomId)
      └─ 없으면 CHAT_001
  → 역할 판별
      ├─ userId == requesterId → chatRoom.leaveAsUser()
      │     └─ userLeft = true → CHAT_004 (이미 나간 경우)
      │     └─ DELETED 상태 → CHAT_005
      └─ 그 외 → TrainerQueryPort.findActiveTrainerUserId(trainerProfileId)
            ├─ 트레이너 userId == requesterId → chatRoom.leaveAsTrainer()
            │     └─ trainerLeft = true → CHAT_004
            │     └─ DELETED 상태 → CHAT_005
            └─ 불일치 → CHAT_003
  → ChatRoomRepository.leaveChatRoom(chatRoom)
      └─ 한 명만 나감: status = CLOSED, 해당 left = true
      └─ 양쪽 모두 나감: status = DELETED, deletedAt 설정
  → 200 응답
```

### 채팅방 상태 전이

| 상황 | 결과 상태 |
| --- | --- |
| 한 명이 나감 | `CLOSED` |
| 양쪽 모두 나감 | `DELETED` |
| 이미 DELETED | `CHAT_005` 오류 |

---

## 4. 채팅방 목록 조회 (`GET /api/chat/rooms`)

```text
회원 또는 트레이너
  → ChatRoomController
  → ChatRoomService.getChatRooms(requesterId)
  → ChatRoomRepository.findChatRoomsByRequesterId(requesterId)
      └─ 네이티브 쿼리: 채팅방 + 마지막 메시지 + 안읽은수 + 상대방 정보 한 번에 조회
  → 트레이너 프로필 이미지 fileId 목록 추출
  → FileUrlUseCase.getUrls(fileIds, requesterId, false)   ← 이미지 URL 일괄 조회
  → ChatRoomSummary 목록에 URL 주입
  → totalUnreadCount = rooms stream 합산
  → 200 응답
```

### 단계별 설명

1. 단일 네이티브 쿼리로 채팅방 목록·마지막 메시지·안읽은수를 한 번에 조회합니다.
2. 트레이너 프로필 이미지는 fileId 목록을 모아 `FileUrlUseCase.getUrls()`로 일괄 조회합니다. (N+1 방지)
3. `totalUnreadCount`는 목록 조회 결과를 Java 레이어에서 합산합니다.
4. `DELETED` 상태 채팅방은 쿼리 단계에서 제외됩니다.
5. 이미 나간 채팅방(`userLeft = true` 또는 `trainerLeft = true`)도 본인 기준으로 제외됩니다.

---

## 5. 전체 안 읽은 메시지 수 조회 (`GET /api/chat/rooms/unread-count`)

```text
회원 또는 트레이너
  → ChatRoomController
  → ChatRoomService.getTotalUnreadCount(requesterId)
  → ChatRoomRepository.countTotalUnread(requesterId)
      └─ 네이티브 COUNT 쿼리:
         chat_messages JOIN chat_rooms JOIN trainer_profiles
         WHERE 본인 참여방 AND NOT 나간방 AND sender != 본인 AND is_read = false
  → 200 응답 (totalUnreadCount)
```

### 단계별 설명

1. 채팅방 목록 조회 없이 직접 COUNT 쿼리 한 번으로 처리합니다. (헤더 배지용 경량 API)
2. DELETED 상태 채팅방과 본인이 나간 채팅방은 제외됩니다.

---

## 6. 채팅 메시지 목록 조회 (`GET /api/chat/rooms/{chatRoomId}/messages`)

```text
회원 또는 트레이너
  → ChatMessageController
  → ChatMessageService.findMessages(requesterId, query)
  → validateParticipant(chatRoomId, requesterId)          ← 참여자 검증 (공통 메서드)
      → ChatRoomRepository.findById(chatRoomId)
          └─ 없으면 CHAT_001
      → status != ACTIVE → CHAT_003
      → userId != requesterId → TrainerQueryPort로 트레이너 userId 조회 → 불일치 시 CHAT_003
  → ChatMessageRepository.findMessages(query, requesterId)
      └─ cursor 없음: 최신 메시지 size개 조회
      └─ cursor 있음: cursor 미만 메시지 size개 조회
  → 200 응답 (messages, nextCursor, hasNext)
```

### 커서 페이지네이션 방식

| 상황 | 동작 |
| --- | --- |
| `cursor` 없음 | 가장 최신 메시지부터 `size`개 조회 |
| `cursor` 있음 | `messageId < cursor` 조건으로 이전 메시지 조회 |
| `hasNext = true` | `nextCursor` 값으로 다음 요청 |
| `hasNext = false` | 더 이상 과거 메시지 없음 |

---

## 7. 참여자 검증 공통 흐름

메시지 목록 조회와 WebSocket 메시지 전송 모두 `validateParticipant()`를 공유합니다.

```text
validateParticipant(chatRoomId, userId)
  → ChatRoomRepository.findById(chatRoomId)  ← 없으면 CHAT_001
  → status != ACTIVE                         ← CHAT_003 (CLOSED/DELETED)
  → userId == chatRoom.userId → 통과 (USER)
  → 아니면 TrainerQueryPort.findActiveTrainerUserId(trainerProfileId)
      → 없으면 CHAT_006
      → trainerUserId == userId → 통과 (TRAINER)
      → 아니면 CHAT_003
```

---

## 📝 문서 정보

- 작성일: `2026-07-21`
