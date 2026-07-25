# 챗봇 API Flow

이 문서는 챗봇 REST 조회 API와 실시간 WebSocket(STOMP) 메시지 API의 내부 호출 흐름을 정리한다.

- REST 외부 계약: [API.md](API.md)
- WebSocket 외부 계약: [WEBSOCKET_API.md](WEBSOCKET_API.md)
- WebSocket 이벤트 상세: [WEBSOCKET_FLOW.md](WEBSOCKET_FLOW.md)

---

## 1. 공통 레이어 구조

```text
Presentation Controller
  → UseCase Interface
  → Command / Query
  → Application Service
  → Domain Repository Interface
  → Persistence Adapter
  → Spring Data Repository
```

- REST Controller는 HTTP 요청·응답만 처리하고, WebSocket Controller는 STOMP 요청·이벤트 전달만 처리한다.
- Application Service는 유스케이스와 트랜잭션 경계를 담당한다.
- Application/Domain 계층은 JPA Entity 또는 Spring Data Repository를 직접 참조하지 않고, Repository 인터페이스를 통해 접근한다.
- REST 요청의 사용자 정보는 `@AuthenticationPrincipal AuthUser`, WebSocket 요청의 사용자 정보는 STOMP `Principal`에서 꺼낸 `AuthUser`를 사용한다. 클라이언트가 `userId`를 전달하거나 지정할 수 없다.

---

## 2. 챗봇 세션 목록 조회 Flow

`GET /api/chatbot/sessions?cursor={cursor}&size={size}`

```text
ChatbotSessionController.findSessions()
  → ChatbotSessionQueryUseCase.findSessions(FindChatbotSessionsQuery)
  → ChatbotSessionQueryService.findSessions()
  → ChatbotSessionRepository.findSessionSummaries()
  → ChatbotSessionPersistenceAdapter
  → SpringDataChatbotSessionRepository.findSessionList()
```

1. `FindChatbotSessionsRequest`가 `cursor`, `size`를 바인딩하고 `size` 범위를 `1~50`으로 검증한다. 기본값은 `20`이다.
2. Controller가 인증 사용자 ID와 요청값으로 `FindChatbotSessionsQuery`를 생성해 UseCase에 전달한다.
3. Service가 `(lastActivityAt, sessionId)` 커서를 해석한다. 형식이 잘못되면 `InvalidChatbotSessionCursorException`을 통해 `COMMON_400`을 반환한다.
4. Domain Repository가 `size + 1`개를 조회하고, Persistence Adapter가 조회 결과를 `ChatbotSessionSummary` 도메인 모델로 변환한다.
5. Spring Data Query는 로그인 사용자가 소유한 세션만 대상으로 첫 USER 메시지와 최신 메시지를 함께 조회한다. 정렬은 `lastActivityAt DESC, sessionId DESC`이다.
6. Service가 한 건을 더 조회한 결과로 `hasNext`, `nextCursor`를 계산하고 `ChatbotSessionListResult`를 반환한다.
7. Controller가 `ChatbotSessionListResponse`를 `GlobalApiResponse`로 감싸 HTTP 응답한다.

---

## 3. 챗봇 메시지 이력 조회 Flow

`GET /api/chatbot/sessions/{sessionId}/messages?cursor={cursor}&size={size}`

```text
ChatbotSessionController.findMessages()
  → ChatbotMessageQueryUseCase.findMessages(FindChatbotMessagesQuery)
  → ChatbotMessageQueryService.findMessages()
  → ChatbotSessionRepository.findBySessionId()
  → ChatbotMessageRepository.findHistory()
  → ChatbotSessionPersistenceAdapter / ChatbotMessagePersistenceAdapter
  → SpringDataChatbotSessionRepository / SpringDataChatbotMessageRepository
```

1. `FindChatbotMessagesRequest`가 `cursor`, `size`를 바인딩하고 `size` 범위를 `1~50`으로 검증한다. 기본값은 `20`이다.
2. Controller가 인증 사용자 ID, `sessionId`, 요청값으로 `FindChatbotMessagesQuery`를 생성한다.
3. Service가 세션을 조회하고 소유권을 검증한다.
   - 세션이 없으면 `CHATBOT_SESSION_NOT_FOUND`(404)를 반환한다.
   - 다른 사용자의 세션이면 `CHATBOT_SESSION_ACCESS_DENIED`(403)를 반환한다.
4. Service가 `(createdAt, messageId)` 커서를 해석한다. 잘못된 커서는 `InvalidChatbotMessageCursorException`을 통해 `COMMON_400`을 반환한다.
5. `ChatbotMessageRepository.findHistory()`가 최신순으로 `size + 1`개를 조회한다.
6. Service는 조회 결과를 오래된 메시지부터 보이도록 역순으로 정리하고, 다음 과거 페이지가 있으면 가장 오래된 메시지를 기준으로 `nextCursor`를 만든다.
7. `routineJson`, `sourcesJson`은 JSON으로 파싱해 응답 DTO의 `routine`, `sources`에 전달한다. 이력이 없는 정상 세션은 빈 목록과 `hasNext=false`, `nextCursor=null`을 반환한다.

---

## 4. 실시간 챗봇 메시지 WebSocket Flow

### 4.1 대상 경로

```text
WebSocket 연결: /ws
클라이언트 전송: /app/chatbot.send
클라이언트 구독: /user/queue/chatbot
Spring → FastAPI 내부 호출: POST /api/v1/chatbot/messages (SSE)
```

### 4.2 전체 호출 흐름

```text
Frontend
  → STOMP SEND /app/chatbot.send
  → ChatbotWebSocketController.sendMessage()
  → ChatbotConversationService.prepare()
      → 챗봇 접근 권한 검증(활성·미만료 구독권 또는 ACTIVE 트레이너 프로필)
      → 세션 생성 또는 세션 소유권 검증
      → 세션 단위 스트림 잠금 획득
      → quickReply 검증 및 선택값 컨텍스트 저장
      → USER 메시지 저장
      → 최근 메시지·활성 컨텍스트 조회 후 FastAPI memory 구성
  → /user/queue/chatbot started 전송
  → chatbotStreamingTaskExecutor 비동기 실행
      → ChatbotFastApiClientAdapter.stream()
      → POST FastAPI /api/v1/chatbot/messages
      → 동일 HTTP 연결의 SSE 수신
          → delta: STOMP delta 즉시 릴레이
          → delta가 0건인 done: done.answer를 STOMP delta로 한 번 보완 릴레이
          → done: ASSISTANT 메시지·선택지 컨텍스트 저장 후 STOMP done 릴레이
          → error: STOMP error 릴레이
  → finally: 세션 스트림 잠금 해제
```

### 4.3 요청 준비 단계

1. `ChatbotWebSocketController`가 STOMP Principal에서 `AuthUser`를 추출하고, 요청 DTO를 `SendChatbotMessageCommand`로 변환한다.
2. `ChatbotConversationService.prepare()`가 활성·미만료 구독권 또는 ACTIVE 트레이너 프로필 보유 여부를 먼저 확인한다.
   - 실패 시 `CHATBOT_SUBSCRIPTION_REQUIRED` 오류를 반환한다.
   - 이 경우 세션 생성·조회, 메시지 저장, FastAPI 호출은 모두 수행하지 않는다.
3. `sessionId`가 없으면 새 `chatbot_session`을 생성한다. 있으면 세션 존재 여부와 로그인 사용자의 소유권을 검증한다.
4. 세션 단위 스트림 잠금을 획득한다. 같은 세션의 응답이 생성 중이면 `CHATBOT_STREAM_IN_PROGRESS` 오류를 반환한다. 서로 다른 세션은 독립적으로 처리한다.
5. `quickReply`가 포함된 요청은 현재 보낸 버튼 선택인지 검증한다.
   - `chatbot_contexts`의 `ROUTINE_PREFERENCE`에 저장된 현재 질문과 허용된 값만 선택할 수 있다.
   - 유효한 선택값은 기존 컨텍스트 행에 갱신하며, 별도 DB 마이그레이션은 필요하지 않다.
   - 잘못되었거나 만료된 선택값은 `CHATBOT_INVALID_QUICK_REPLY` 오류를 반환하고, 메시지 저장과 FastAPI 호출을 수행하지 않는다.
6. Service가 USER 메시지를 저장하고, 최근 메시지와 활성 `chatbot_contexts`를 조회해 FastAPI 요청의 `memory`를 구성한다.

### 4.4 Spring → FastAPI → Frontend 스트리밍

1. Controller는 `started(sessionId, requestId)` 이벤트를 `/user/queue/chatbot`으로 먼저 전송한다.
2. 전용 `chatbotStreamingTaskExecutor`에서 `ChatbotFastApiClientAdapter`가 FastAPI에 한 번의 POST 요청을 보낸다.
3. Spring은 동일 HTTP 연결에서 FastAPI SSE를 종료 시점까지 읽는다. SSE 이벤트마다 새로운 HTTP 요청을 만들지 않는다.

| FastAPI SSE | Spring 처리 | Frontend STOMP 이벤트 |
| --- | --- | --- |
| `delta` | 텍스트 조각 즉시 전달 | `delta` |
| `done` | ASSISTANT 메시지 저장, 선택지 컨텍스트 저장 | `done` |
| `error` | 메시지 저장 없이 오류 전달 | `error` |

4. `done` 수신 시에만 AI의 최종 `answer`, `routine`, `sources`를 ASSISTANT 메시지로 저장한다. 따라서 FastAPI 오류나 스트림 실패 시 불완전한 assistant 메시지는 남지 않는다.
5. Spring은 FastAPI delta를 지연·버퍼링하지 않고 즉시 릴레이한다. 다만 요청 동안 delta가 없었고 done이 오면, 빈 응답을 방지하기 위해 `done.answer`를 delta로 한 번 전송한 뒤 done을 전달한다. delta가 하나 이상 있었다면 `done.answer`를 다시 delta로 보내지 않는다.
6. 프론트는 수신한 delta를 렌더링 큐에 보관하고 일정 간격으로 화면에 출력한다. 타이핑 효과의 속도 제어는 Spring이 아닌 프론트의 책임이다.
7. `finally`에서 성공·실패와 관계없이 세션 스트림 잠금을 해제한다.

### 4.5 quickReply 상태 흐름

```text
FastAPI quick_replies
  → Spring ChatbotAiEvent.Done.quickRepliesJson
  → Spring이 question_id를 questionId로 정규화
  → STOMP done.quickReplies
  → 프론트가 label을 버튼으로 렌더링
  → 클릭 시 questionId, value를 quickReply로 /app/chatbot.send 전송
  → Spring이 선택지 검증 후 chatbot_contexts.ROUTINE_PREFERENCE 갱신
  → 갱신된 컨텍스트가 다음 FastAPI memory에 포함
  → 다음 질문 선택지 또는 최종 루틴 응답
```

- 인사(`GREETING`)와 루틴 선택 단계는 FastAPI가 고정 응답과 `quickReplies`로 처리한다.
- 루틴 선택은 운동 목표 → 주당 운동일 → 운동 장소 순서다.
- 선택 단계에서는 LLM/RAG를 호출하지 않으며, 모든 선택값이 갖춰진 뒤에 최종 루틴 추천을 생성한다.
- FastAPI는 Spring이 전달한 `memory`를 기준으로 응답하며, FastAPI 자체 인메모리 대화 상태에 의존하지 않는다.

### 4.6 주요 실패 경로

| 상황 | 처리 |
| --- | --- |
| 활성·미만료 구독권과 ACTIVE 트레이너 프로필 모두 없음 | `CHATBOT_SUBSCRIPTION_REQUIRED` 오류, 세션/메시지/FastAPI 호출 없음 |
| 세션 없음 또는 타인 소유 | `CHATBOT_SESSION_NOT_FOUND` 또는 `CHATBOT_SESSION_ACCESS_DENIED` 오류 |
| 잘못되었거나 만료된 버튼 선택 | `CHATBOT_INVALID_QUICK_REPLY` 오류, 선택값/메시지/FastAPI 호출 없음 |
| 같은 세션 중복 요청 | `CHATBOT_STREAM_IN_PROGRESS` 오류 |
| 스트리밍 실행기 포화 | `CHATBOT_STREAM_CAPACITY_EXCEEDED` 오류, 스트림 잠금 해제 |
| FastAPI 연결·응답 오류 | `CHATBOT_502_1`, `CHATBOT_502_2`, `CHATBOT_504_1` 또는 FastAPI 오류를 `error` 이벤트로 릴레이 |

---

## 5. 범위 제외

- FastAPI 내부 LangGraph, RAG, LLM 호출의 세부 구현
- 프론트 화면의 스트리밍 텍스트·선택 버튼 렌더링 구현
- 챗봇 외 도메인의 WebSocket 이벤트
