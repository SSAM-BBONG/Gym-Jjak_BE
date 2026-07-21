# 💬 채팅 REST API

> 작성일: 2026-07-21
> 대상: `ChatRoomController`, `ChatMessageController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 기본 경로: `/api/chat/rooms`

## 기능 개요

- 회원(USER)이 트레이너와 1:1 채팅방을 생성합니다.
- 회원·트레이너(USER/TRAINER)가 채팅방 목록 및 안 읽은 메시지 수를 조회합니다.
- 과거 메시지를 커서 기반 페이지네이션으로 조회합니다.
- 채팅방에서 나갑니다.

> 실시간 메시지 전송은 WebSocket(STOMP) 방식으로 처리됩니다. → [WEBSOCKET_API.md](WEBSOCKET_API.md)

---

## 1. 채팅방 목록 조회

`GET /api/chat/rooms`

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (USER 또는 TRAINER 권한) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `CHAT_ROOM_003` | 채팅방 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "CHAT_ROOM_003",
  "message": "채팅방 목록 조회 성공",
  "data": {
    "totalCount": 2,
    "totalUnreadCount": 5,
    "chatRooms": [
      {
        "chatRoomId": 1,
        "partnerName": "김트레이너",
        "partnerRole": "TRAINER",
        "partnerProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/...",
        "lastMessage": "안녕하세요!",
        "lastMessageAt": "2026-07-21T10:00:00",
        "unreadCount": 3
      }
    ]
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.totalCount` | 참여 중인 채팅방 수 |
| `data.totalUnreadCount` | 전체 채팅방의 안 읽은 메시지 수 합산 |
| `data.chatRooms[].chatRoomId` | 채팅방 ID |
| `data.chatRooms[].partnerName` | 대화 상대 이름 |
| `data.chatRooms[].partnerRole` | 대화 상대 역할. `TRAINER` 또는 `USER` |
| `data.chatRooms[].partnerProfileImageUrl` | 대화 상대 프로필 이미지 URL (없으면 `null`) |
| `data.chatRooms[].lastMessage` | 마지막 메시지 내용 (없으면 `null`) |
| `data.chatRooms[].lastMessageAt` | 마지막 메시지 전송 시각 (없으면 `null`) |
| `data.chatRooms[].unreadCount` | 해당 채팅방의 안 읽은 메시지 수 |

> 목록 정렬: `lastMessageAt` 내림차순. 메시지가 없는 채팅방은 하단에 표시됩니다.

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | USER/TRAINER 권한 없음 |

---

## 2. 전체 안 읽은 메시지 수 조회

`GET /api/chat/rooms/unread-count`

> 헤더 배지 등 가벼운 폴링에 적합. DB COUNT 쿼리 단일 호출로 처리됩니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (USER 또는 TRAINER 권한) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `CHAT_ROOM_004` | 안 읽은 메시지 수 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "CHAT_ROOM_004",
  "message": "안 읽은 메시지 수 조회 성공",
  "data": {
    "totalUnreadCount": 5
  }
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | USER/TRAINER 권한 없음 |

---

## 3. 채팅방 생성

`POST /api/chat/rooms`

> 🔒 USER 권한 필요. 동일 트레이너·PT코스 조합의 ACTIVE 채팅방이 이미 있으면 생성 불가.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (USER 권한) |

Request Body

```json
{
  "trainerProfileId": 10,
  "ptCourseId": 5
}
```

| name | 필수 | description |
| --- | --- | --- |
| `trainerProfileId` | O | 대화할 트레이너의 프로필 ID (양수) |
| `ptCourseId` | O | 연결할 PT 코스 ID (양수) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `201 Created` | `CHAT_ROOM_001` | 채팅방 생성 성공 |

Response Body

```json
{
  "status": 201,
  "code": "CHAT_ROOM_001",
  "message": "채팅방 생성 성공",
  "data": {
    "chatRoomId": 1
  }
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | 필수값 누락 또는 0 이하 값 |
| `400 Bad Request` | `CHAT_007` | 유효하지 않은 PT 코스입니다. | 존재하지 않는 PT 코스 ID |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | USER 권한 없음 |
| `404 Not Found` | `CHAT_006` | 해당 트레이너를 찾을 수 없습니다. | 존재하지 않는 트레이너 프로필 ID |
| `409 Conflict` | `CHAT_002` | 이미 해당 트레이너와의 채팅방이 존재합니다. | 동일 조합의 ACTIVE 채팅방 존재 |

---

## 4. 채팅방 나가기

`PATCH /api/chat/rooms/{chatRoomId}/leave`

> 한 명이 나가면 CLOSED, 양쪽 모두 나가면 DELETED 상태로 전환됩니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (USER 또는 TRAINER 권한) |

Path Variable

| name | description |
| --- | --- |
| `chatRoomId` | 나갈 채팅방 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `CHAT_ROOM_002` | 채팅방 나가기 성공 |

Response Body

```json
{
  "status": 200,
  "code": "CHAT_ROOM_002",
  "message": "채팅방 나가기 성공",
  "data": null
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `CHAT_005` | 이미 종료된 채팅방입니다. | DELETED 상태의 채팅방 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `CHAT_003` | 해당 채팅방에 접근할 권한이 없습니다. | 채팅방 참여자가 아님 |
| `404 Not Found` | `CHAT_001` | 채팅방을 찾을 수 없습니다. | 존재하지 않는 채팅방 ID |
| `409 Conflict` | `CHAT_004` | 이미 나간 채팅방입니다. | 이미 나간 상태 |

---

## 5. 채팅 메시지 목록 조회

`GET /api/chat/rooms/{chatRoomId}/messages?cursor=100&size=20`

> 커서 기반 페이지네이션. cursor 미입력 시 최신 메시지부터 조회합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (USER 또는 TRAINER 권한) |

Path Variable

| name | description |
| --- | --- |
| `chatRoomId` | 조회할 채팅방 ID |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `cursor` | X | 이전 응답의 `nextCursor`. 없으면 최신 메시지부터 조회 (1 이상) |
| `size` | X | 조회할 메시지 수 (기본값: 20, 최소: 1, 최대: 50) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `CHAT_MESSAGE_001` | 채팅 메시지 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "CHAT_MESSAGE_001",
  "message": "채팅 메시지 목록 조회 성공",
  "data": {
    "messages": [
      {
        "messageId": 100,
        "senderId": 1,
        "content": "안녕하세요!",
        "read": true,
        "createdAt": "2026-07-21T10:00:00"
      }
    ],
    "nextCursor": 80,
    "hasNext": true
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.messages[].messageId` | 메시지 ID |
| `data.messages[].senderId` | 발신자 사용자 ID |
| `data.messages[].content` | 메시지 내용 |
| `data.messages[].read` | 읽음 여부 |
| `data.messages[].createdAt` | 전송 시각 |
| `data.nextCursor` | 다음 페이지 조회 시 사용할 cursor 값. 다음 페이지 없으면 `null` |
| `data.hasNext` | 다음 페이지 존재 여부 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `CHAT_003` | 해당 채팅방에 접근할 권한이 없습니다. | 채팅방 참여자가 아님 또는 CLOSED/DELETED 상태 |
| `404 Not Found` | `CHAT_001` | 채팅방을 찾을 수 없습니다. | 존재하지 않는 채팅방 ID |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
