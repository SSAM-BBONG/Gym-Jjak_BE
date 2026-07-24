# 🔄 챗봇 WebSocket 내부 흐름

- 작성일: 2026-07-23
- 최종 수정일: 2026-07-24

> 클라이언트 이벤트 형식은 [WEBSOCKET_API.md](WEBSOCKET_API.md), 세션 조회 REST 계약은 [API.md](API.md)를 참고합니다.
>
> **현재 구현 기준:** 아래 흐름의 `ChatbotWebSocketController`, `ChatbotConversationService`, Port/Adapter, 전용 executor는 Spring 코드로 구현되어 있습니다.

## 🧭 메시지 처리 흐름

```text
클라이언트
  → STOMP SEND /app/chatbot.send
  → ChatbotWebSocketController(인증 Principal)
  → ChatbotConversationService.prepare(...)
      → 활성 챗봇 구독 검증
          → 미보유: /user/queue/chatbot error(CHATBOT_SUBSCRIPTION_REQUIRED) 전송, FastAPI 호출 없음
          → 보유: 아래 세션 처리 계속
  → sessionId 유무 판단
      → 없음: UUID 생성, chatbot_session 생성
      → 있음: 세션 존재·소유자 검증
  → session 단위 진행 중 스트림 잠금 획득
      → 실패: /user/queue/chatbot error(CHATBOT_STREAM_IN_PROGRESS)
  → /user/queue/chatbot started(sessionId, requestId)
  → quickReply 존재 시
      → 현재 ROUTINE_PREFERENCE의 질문·허용 값 검증
      → 선택값을 기존 chatbot_contexts 행에 갱신(30일 만료)
  → chatbot_message(USER) 저장 및 last_activity_at 갱신
  → 방금 저장한 USER 메시지를 제외한 직전 12개 메시지 조회
  → 전용 async executor
      → POST FastAPI /api/v1/chatbot/messages
      → X-Internal-Api-Key, 서버 생성 X-Request-ID, actor, memory 전달
      → 동일 HTTP 응답의 SSE를 종료까지 소비
          → delta: 사용자 전용 큐로 즉시 릴레이
          → done: assistant 최종 메시지 저장
              → ROUTINE quickReplies가 있으면 현재 질문·선택지 저장
              → quickReplies JSON 배열을 포함한 done 릴레이
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
- FastAPI의 `quick_replies`는 Spring의 `ChatbotAiEvent.Done`을 거쳐 STOMP `done.quickReplies` JSON 배열로 전달됩니다. 이때 Spring이 FastAPI의 `question_id`를 프론트 계약인 `questionId`로 정규화합니다. 프론트는 이 배열만 버튼으로 렌더링합니다.
- FastAPI는 Spring이 요청의 `memory`로 전달한 summary, 최근 메시지, 활성 컨텍스트를 사용하며 자체 InMemory 대화 상태를 저장하지 않습니다.

## 🔒 동시성 및 실패 경로

| 상황 | 처리 |
| --- | --- |
| 활성 챗봇 구독 미보유 | `prepare()` 첫 단계에서 `CHATBOT_SUBSCRIPTION_REQUIRED` error를 전송하고, 세션 생성·조회, 스트림 잠금, 메시지 저장 및 FastAPI 호출을 수행하지 않음 |
| 같은 세션의 동시 요청 | 두 번째 요청은 `CHATBOT_STREAM_IN_PROGRESS` error; 새 FastAPI 호출 없음 |
| 다른 세션의 요청 | 독립적으로 실행 가능 |
| FastAPI 연결/파싱/타임아웃 실패 | assistant 미저장, `error` 전송, 잠금 해제 |
| FastAPI `error` | assistant 미저장, code/message/retryable 릴레이, 잠금 해제 |
| 클라이언트 연결 종료 | v1에서는 FastAPI 호출을 취소하지 않음; 서버는 종료까지 처리·저장하고 재연결 뒤 이력 API로 조회 |
| Spring 저장 실패 | 성공 `done`을 보내지 않고 `error(INTERNAL_ERROR)`; 트랜잭션 롤백 |
| 잘못된 quickReply | `CHATBOT_INVALID_QUICK_REPLY` error; 선택값 저장 및 FastAPI 호출 없음 |

## 🧵 트랜잭션 경계

1. 세션 생성/검증과 USER 메시지 저장은 짧은 트랜잭션으로 완료합니다.
2. 네트워크 스트리밍은 DB 트랜잭션 밖의 전용 executor에서 수행합니다.
3. `done` 수신 후 ASSISTANT 메시지 저장과 세션 활동 갱신은 별도 짧은 트랜잭션으로 수행합니다.
4. 잠금 해제는 성공·실패와 관계없이 `finally`에서 수행하되, `active_request_id` 일치 조건으로만 처리합니다. 만료 잠금은 정리 작업이 회수합니다.

## 📝 변경 이력

| 날짜 | 변경 내용 |
| --- | --- |
| 2026-07-24 | Spring 컨텍스트 기반 quickReply 검증·저장, `done.quickReplies` 릴레이 흐름 추가 |
| 2026-07-23 | STOMP 입력부터 FastAPI SSE 릴레이, 저장 순서, 동시성·트랜잭션 경계 작성 |
