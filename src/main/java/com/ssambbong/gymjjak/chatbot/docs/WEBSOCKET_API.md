# 🤖 챗봇 WebSocket(STOMP) API

- 작성일: 2026-07-23
- 최종 수정일: 2026-07-23
- 프로토콜: STOMP over SockJS
- WebSocket 엔드포인트: `ws://{host}/ws`

> 연결·쿠키 인증 방식은 기존 채팅 WebSocket과 같습니다. `JwtHandshakeInterceptor`와 `JwtChannelInterceptor`가 `accessToken` 쿠키를 인증 Principal로 변환합니다.
>
> **현재 구현 기준:** 아래 destination과 이벤트는 `chatbot` 도메인에서 구현되어 있습니다. 기존 `chat` WebSocket과는 목적과 destination이 분리되어 있어 기존 채팅 WebSocket에는 존재하지 않습니다.

## 📬 구독

연결 후 사용자 전용 큐를 먼저 구독합니다.

```text
/user/queue/chatbot
```

```javascript
stompClient.subscribe('/user/queue/chatbot', (message) => {
  const event = JSON.parse(message.body);
  handleChatbotEvent(event);
});
```

다른 사용자의 이벤트는 이 destination으로 수신할 수 없습니다. 기존 1:1 채팅용 `/topic/chat.room.{chatRoomId}` 및 `/user/queue/errors`와 목적이 다릅니다.

## 📤 전송

```text
/app/chatbot.send
```

```json
{
  "sessionId": "019f0000-0000-7000-8000-000000000001",
  "content": "이번 주 하체 운동 루틴을 추천해줘.",
  "intentHint": "ROUTINE_RECOMMENDATION"
}
```

| 필드 | 필수 | 설명 |
| --- | --- | --- |
| `sessionId` | X | 이어서 대화할 UUID. 생략하면 Spring이 새 UUID 세션을 생성 |
| `content` | O | 공백이 아닌 사용자 질문 |
| `intentHint` | X | 버튼 기반 의도 힌트. FastAPI 계약의 `intent_hint`로 변환 |

```javascript
stompClient.send('/app/chatbot.send', {}, JSON.stringify({
  content: '이번 주 하체 운동 루틴을 추천해줘.',
  intentHint: 'ROUTINE_RECOMMENDATION'
}));
```

## 🌊 수신 이벤트

모든 이벤트는 원칙적으로 `type`, `sessionId`, `requestId`를 포함합니다. 다만 활성 챗봇 구독 사전 검증에 실패한 `CHATBOT_SUBSCRIPTION_REQUIRED` error는 세션과 요청 ID가 생성되기 전에 발생하므로 `sessionId`와 `requestId`가 `null`입니다. 이는 Spring의 `ChatbotErrorEvent.of(null, null, ...)` 동작과 같습니다. 새 세션은 첫 번째 `started` 이벤트에서 클라이언트가 보관할 `sessionId`를 받습니다.

### `started`

```json
{
  "type": "started",
  "sessionId": "019f0000-0000-7000-8000-000000000001",
  "requestId": "019f0000-0000-7000-8000-000000000002"
}
```

### `delta`

FastAPI SSE의 `event: delta`를 그대로 릴레이한 텍스트 조각입니다.

```json
{
  "type": "delta",
  "sessionId": "019f0000-0000-7000-8000-000000000001",
  "requestId": "019f0000-0000-7000-8000-000000000002",
  "text": "이번 주는 하체를 "
}
```

### `done`

최종 assistant 메시지가 저장된 뒤 한 번 전송됩니다. `answer`는 이미 렌더링한 delta를 다시 이어 붙이는 용도가 아니라, 최종 결과 확인·재동기화용 전체 텍스트입니다.

```json
{
  "type": "done",
  "sessionId": "019f0000-0000-7000-8000-000000000001",
  "requestId": "019f0000-0000-7000-8000-000000000002",
  "answer": "이번 주는 하체를 두 번 나누어 진행해 보세요.",
  "category": "ROUTINE",
  "routine": null,
  "sources": [],
  "limited": false
}
```

### `error`

assistant 메시지는 저장하지 않습니다.

```json
{
  "type": "error",
  "sessionId": "019f0000-0000-7000-8000-000000000001",
  "requestId": "019f0000-0000-7000-8000-000000000002",
  "code": "CHATBOT_STREAM_IN_PROGRESS",
  "message": "해당 대화의 응답이 이미 생성 중입니다.",
  "retryable": false
}
```

| code | 설명 |
| --- | --- |
| `CHATBOT_STREAM_IN_PROGRESS` | 같은 세션에서 이전 스트림이 종료되지 않음 |
| `CHATBOT_STREAM_CAPACITY_EXCEEDED` | 전용 스트리밍 executor가 포화되어 새 스트림을 받을 수 없음 |
| `CHATBOT_SESSION_NOT_FOUND` | 존재하지 않거나 본인 소유가 아닌 `sessionId` |
| `CHATBOT_SUBSCRIPTION_REQUIRED` | Spring이 FastAPI 호출 전에 활성 챗봇 구독을 검증해 거부함 |
| `LLM_CALL_LIMIT_EXCEEDED`, `LLM_NETWORK_ERROR`, `INTERNAL_ERROR` | FastAPI SSE `error`의 코드 전달 |

## 📝 변경 이력

| 날짜 | 변경 내용 |
| --- | --- |
| 2026-07-23 | `/app/chatbot.send`, `/user/queue/chatbot`, `started/delta/done/error` 이벤트 계약 작성 |
