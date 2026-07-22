# 🔄 챗봇 WebSocket 내부 흐름

- 작성일: 2026-07-23
- 최종 수정일: 2026-07-23

> 클라이언트 이벤트 형식은 [WEBSOCKET_API.md](WEBSOCKET_API.md), 세션 조회 REST 계약은 [API.md](API.md)를 참고합니다.
>
> **구현 예정 계약:** 아래 흐름의 Controller, UseCase, Port/Adapter, 전용 executor는 아직 Spring 코드에 없습니다.

## 🧭 메시지 처리 흐름

```text
클라이언트
  → STOMP SEND /app/chatbot.send
  → ChatbotWebSocketController(인증 Principal)
  → sessionId 유무 판단
      → 없음: UUID 생성, chatbot_session 생성
      → 있음: 세션 존재·소유자 검증
  → session 단위 진행 중 스트림 잠금 획득
      → 실패: /user/queue/chatbot error(CHATBOT_STREAM_IN_PROGRESS)
  → /user/queue/chatbot started(sessionId, requestId)
  → chatbot_message(USER) 저장 및 last_activity_at 갱신
  → 방금 저장한 USER 메시지를 제외한 직전 12개 메시지 조회
  → 전용 async executor
      → POST FastAPI /api/v1/chatbot/messages
      → X-Internal-Api-Key, 서버 생성 X-Request-ID, actor, memory 전달
      → 동일 HTTP 응답의 SSE를 종료까지 소비
          → delta: 사용자 전용 큐로 즉시 릴레이
          → done: assistant 최종 메시지 저장 → done 릴레이
          → error: assistant 저장 없이 error 릴레이
  → finally: 세션 스트림 잠금 해제
```

## 📡 Spring ↔ FastAPI SSE 변환

Spring은 FastAPI에 요청 한 번을 보내고, 그 **동일한 HTTP 응답 연결**에서 반복되는 SSE 이벤트를 끝까지 읽습니다. SSE 이벤트마다 새 HTTP 요청을 만들지 않습니다.

| FastAPI SSE | Spring 처리 | Frontend STOMP |
| --- | --- | --- |
| `event: delta`, `{"text":"..."}` | 텍스트 조각을 즉시 릴레이 | `type=delta` |
| `event: done`, `ChatResponse` | assistant 최종본 저장 | `type=done` |
| `event: error`, `{code,message,request_id,retryable}` | assistant 미저장, 오류 변환 | `type=error` |

- FastAPI `delta` payload에는 `session_id`가 없으므로 Spring이 현재 처리 중인 세션의 `sessionId`를 붙입니다.
- FastAPI `done`의 `session_id`는 Spring이 요청한 세션과 일치해야 합니다. 다르면 안전하게 `error(INTERNAL_ERROR)`로 종료하고 assistant를 저장하지 않습니다.
- Spring의 `requestId`는 STOMP 처리마다 서버가 새 UUID로 생성하며, `X-Request-ID`와 FastAPI payload의 `request_id`를 같은 추적 키로 사용합니다.
- `done.answer`는 최종 전체 문자열입니다. 앞서 받은 delta와 합쳐 화면에 중복 출력하지 않습니다.

## 🔒 동시성 및 실패 경로

| 상황 | 처리 |
| --- | --- |
| 같은 세션의 동시 요청 | 두 번째 요청은 `CHATBOT_STREAM_IN_PROGRESS` error; 새 FastAPI 호출 없음 |
| 다른 세션의 요청 | 독립적으로 실행 가능 |
| FastAPI 연결/파싱/타임아웃 실패 | assistant 미저장, `error` 전송, 잠금 해제 |
| FastAPI `error` | assistant 미저장, code/message/retryable 릴레이, 잠금 해제 |
| 클라이언트 연결 종료 | v1에서는 FastAPI 호출을 취소하지 않음; 서버는 종료까지 처리·저장하고 재연결 뒤 이력 API로 조회 |
| Spring 저장 실패 | 성공 `done`을 보내지 않고 `error(INTERNAL_ERROR)`; 트랜잭션 롤백 |

## 🧵 트랜잭션 경계

1. 세션 생성/검증과 USER 메시지 저장은 짧은 트랜잭션으로 완료합니다.
2. 네트워크 스트리밍은 DB 트랜잭션 밖의 전용 executor에서 수행합니다.
3. `done` 수신 후 ASSISTANT 메시지 저장과 세션 활동 갱신은 별도 짧은 트랜잭션으로 수행합니다.
4. 잠금 해제는 성공·실패와 관계없이 `finally`에서 수행하되, `active_request_id` 일치 조건으로만 처리합니다. 만료 잠금은 정리 작업이 회수합니다.

## 📝 변경 이력

| 날짜 | 변경 내용 |
| --- | --- |
| 2026-07-23 | STOMP 입력부터 FastAPI SSE 릴레이, 저장 순서, 동시성·트랜잭션 경계 작성 |
