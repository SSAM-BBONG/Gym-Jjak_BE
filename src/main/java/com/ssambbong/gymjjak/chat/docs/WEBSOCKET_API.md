# 💬 채팅 WebSocket(STOMP) API

> 작성일: 2026-07-21
> 프로토콜: STOMP over SockJS
> 엔드포인트: `ws://host/ws` (SockJS fallback 포함)

## 기능 개요

- STOMP 프로토콜로 실시간 채팅 메시지를 전송합니다.
- 채팅방 topic을 구독하여 상대방 메시지를 실시간으로 수신합니다.
- 에러 발생 시 개인 에러 큐로 수신합니다.

> REST API(채팅방 생성·목록·나가기·메시지 조회)는 [API.md](API.md)를 참고합니다.

---

## 1. 연결 (CONNECT)

### 엔드포인트

```
ws://{host}/ws
```

SockJS를 사용하므로 클라이언트는 `SockJS` 라이브러리로 연결합니다.

### 인증 방식

| 환경 | 방식 |
| --- | --- |
| 배포 환경 | `accessToken` 쿠키가 자동으로 핸드셰이크 요청에 포함됨 |
| 로컬 개발 | `withCredentials: true` 설정 필요, 도메인 맞춤 필요 |

> 핸드셰이크 단계에서 `JwtHandshakeInterceptor`가 `accessToken` 쿠키를 읽어 세션에 저장합니다.
> STOMP CONNECT 프레임 수신 시 `JwtChannelInterceptor`가 세션에서 토큰을 꺼내 `Authentication`을 설정합니다.
> 토큰 없음 또는 유효하지 않으면 STOMP CONNECT가 거부됩니다.

### 연결 예시 (JavaScript)

```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, (frame) => {
  console.log('Connected');
});
```

---

## 2. 채팅방 구독

STOMP CONNECT 이후, 입장할 채팅방 topic을 구독합니다.

### Destination

```
/topic/chat.room.{chatRoomId}
```

### 구독 예시

```javascript
stompClient.subscribe(`/topic/chat.room.${chatRoomId}`, (message) => {
  const body = JSON.parse(message.body);
  console.log(body);
});
```

### 수신 메시지 형식 (`ChatMessageBroadcast`)

```json
{
  "messageId": 123,
  "chatRoomId": 1,
  "senderId": 42,
  "content": "안녕하세요!",
  "read": false,
  "createdAt": "2026-07-21T10:00:00"
}
```

| name | 설명 |
| --- | --- |
| `messageId` | 저장된 메시지 ID |
| `chatRoomId` | 채팅방 ID |
| `senderId` | 발신자 사용자 ID |
| `content` | 메시지 내용 |
| `read` | 상대방이 현재 같은 채팅방 topic을 구독 중인 경우 `true` |
| `createdAt` | 메시지 전송 시각 |

---

## 3. 메시지 전송

### Destination

```
/app/chat.send
```

> `/app` 접두사는 서버의 `applicationDestinationPrefixes` 설정에 따릅니다.

### 전송 메시지 형식

```json
{
  "chatRoomId": 1,
  "content": "안녕하세요!"
}
```

| name | 필수 | 제약 | 설명 |
| --- | --- | --- | --- |
| `chatRoomId` | O | 양수 | 메시지를 보낼 채팅방 ID |
| `content` | O | 최대 2000자, 공백 불가 | 메시지 내용 |

### 전송 예시

```javascript
stompClient.send('/app/chat.send', {}, JSON.stringify({
  chatRoomId: 1,
  content: '안녕하세요!'
}));
```

### 처리 결과

- 메시지가 저장되고 `/topic/chat.room.{chatRoomId}`로 브로드캐스트됩니다.
- 현재 해당 topic 구독자가 2명(발신자 + 수신자)이면 `read = true`로 저장됩니다.
- 에러 발생 시 개인 에러 큐(`/user/queue/errors`)로 에러 메시지가 전달됩니다.

---

## 4. 에러 구독

서버에서 발생한 에러는 발신자의 개인 큐로 전달됩니다.

### Destination

```
/user/queue/errors
```

### 에러 구독 예시

```javascript
stompClient.subscribe('/user/queue/errors', (message) => {
  const error = JSON.parse(message.body);
  console.error(error.code, error.message);
});
```

### 에러 메시지 형식 (`ChatErrorResponse`)

```json
{
  "timestamp": "2026-07-21T10:00:00",
  "code": "CHAT_003",
  "message": "해당 채팅방에 접근할 권한이 없습니다."
}
```

| name | 설명 |
| --- | --- |
| `timestamp` | 에러 발생 시각 |
| `code` | 에러 코드 |
| `message` | 에러 메시지 |

### 발생 가능한 에러 코드

| code | message | 설명 |
| --- | --- | --- |
| `CHAT_001` | 채팅방을 찾을 수 없습니다. | 존재하지 않는 chatRoomId |
| `CHAT_003` | 해당 채팅방에 접근할 권한이 없습니다. | 참여자가 아닌 채팅방, 또는 CLOSED/DELETED 상태 |
| `CHAT_006` | 해당 트레이너를 찾을 수 없습니다. | 트레이너 정보 조회 실패 |
| `INTERNAL_ERROR` | 메시지 처리 중 오류가 발생했습니다. | 예상치 못한 서버 오류 |

---

## 5. STOMP Broker 설정 요약

| 항목 | 값 |
| --- | --- |
| WebSocket 엔드포인트 | `/ws` |
| SockJS | 지원 |
| 브로드캐스트 prefix | `/topic` |
| 개인 메시지 prefix | `/queue` |
| 사용자 destination prefix | `/user` |
| 서버 수신 prefix | `/app` |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
