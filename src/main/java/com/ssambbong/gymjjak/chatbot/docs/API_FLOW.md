# 챗봇 REST API Flow

이 문서는 REST 조회 API의 호출 흐름을 정리한다. 메시지 생성, FastAPI 호출, STOMP 스트리밍은 이 범위에 포함하지 않으며 [WEBSOCKET_FLOW.md](WEBSOCKET_FLOW.md)를 따른다.

---

## 1. 공통 레이어 구조

```text
Controller
→ UseCase 인터페이스
→ Query
→ Service
→ Domain Repository 인터페이스
→ Persistence Adapter
→ Spring Data Repository
```

Controller는 HTTP 요청·응답만 처리하고, Service는 JPA Entity나 Spring Data 타입을 직접 참조하지 않는다. Domain Repository는 application/domain과 infrastructure를 분리하며, Adapter가 Spring Data 조회 결과를 Domain Model로 변환한다.

JWT 인증은 `SecurityConfig`와 JWT 필터가 처리한다. 인증이 성공하면 `@AuthenticationPrincipal AuthUser`로 사용자 ID를 전달하며, 요청에서 `userId`를 받지 않으므로 타 사용자 ID를 직접 지정할 수 없다.

---

## 2. 세션 목록 조회 Flow

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
2. Controller가 인증 사용자 ID와 요청 값을 `FindChatbotSessionsQuery`로 만들어 UseCase에 전달한다.
3. Service가 `(lastActivityAt, sessionId)` cursor를 해석한다. 잘못된 Base64, JSON, JSON `null`, 필수 값 누락은 `InvalidChatbotSessionCursorException`으로 변환되어 `COMMON_400`이 된다.
4. Domain Repository는 `size + 1`개를 조회한다. Adapter가 Spring Data native query 결과를 `ChatbotSessionSummary`로 매핑한다.
5. Spring Data query는 사용자 소유 세션만 대상으로 첫 `USER` 메시지와 최신 메시지를 함께 읽는다. 정렬은 `lastActivityAt DESC, sessionId DESC`이며 목록 조회 중 N+1이 발생하지 않는다.
6. Service가 `hasNext`, `nextCursor`를 계산하고 `ChatbotSessionListResult`를 반환한다.
7. `ChatbotSessionListResponse`가 HTTP 응답 DTO로 변환되고 `GlobalApiResponse`로 감싸져 반환된다.

---

## 3. 메시지 이력 조회 Flow

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
2. Controller가 `AuthUser.userId()`, path의 `sessionId`, 요청값으로 `FindChatbotMessagesQuery`를 생성한다.
3. Service가 먼저 `ChatbotSessionRepository.findBySessionId()`로 세션을 조회한다.
   - 세션이 없으면 `CHATBOT_SESSION_NOT_FOUND`(404)를 반환한다.
   - 세션의 `isOwnedBy(userId)`가 false이면 `CHATBOT_SESSION_ACCESS_DENIED`(403)를 반환한다.
4. `(createdAt, messageId)` cursor를 해석한다. 잘못된 cursor는 `InvalidChatbotMessageCursorException`을 통해 `COMMON_400`으로 반환한다.
5. `ChatbotMessageRepository.findHistory()`가 최신순 `createdAt DESC, messageId DESC`으로 `size + 1`개를 조회한다. Persistence Adapter는 JPA Entity를 `ChatbotMessage` Domain Model로 변환한다.
6. Service는 조회 대상 `size`개를 오래된 순으로 뒤집어 `ChatbotMessageHistoryResult`로 반환한다. 더 과거 메시지가 있으면 응답의 가장 오래된 메시지를 기준으로 `nextCursor`를 만든다.
7. `routineJson`, `sourcesJson`은 Service에서 JSON으로 파싱해 `routine`, `sources`로 전달한다. `sourcesJson`이 없으면 빈 JSON 배열을 반환한다.
8. `ChatbotMessageHistoryResponse`가 HTTP DTO로 변환되고 `GlobalApiResponse`로 감싸져 반환된다. 빈 세션 이력은 빈 목록과 `hasNext=false`, `nextCursor=null`이다.

---

## 4. 범위 제외

- STOMP `started`, `delta`, `done`, `error` 이벤트 처리
- FastAPI 챗봇 응답 스트리밍 및 재시도
- 챗봇 메시지 저장을 담당하는 `ChatbotConversationService`
- DB migration 변경
