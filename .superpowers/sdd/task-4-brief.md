# Task 4 — 챗봇 메시지 이력 REST API와 문서

## Scope

Task 3의 `ChatbotMessageQueryUseCase`를 `GET /api/chatbot/sessions/{sessionId}/messages`로 노출한다. 기존 session-list endpoint는 유지한다.

Create:
- `chatbot/presentation/api/request/FindChatbotMessagesRequest.java`
- `chatbot/presentation/api/response/ChatbotMessageHistoryItemResponse.java`
- `chatbot/presentation/api/response/ChatbotMessageHistoryResponse.java`

Modify:
- `chatbot/presentation/api/ChatbotSessionController.java`
- `chatbot/presentation/api/response/ChatbotResponseCode.java`
- `src/test/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionControllerTest.java`
- `chatbot/docs/API.md`
- `chatbot/docs/API_FLOW.md`

## Endpoint

```java
@GetMapping("/{sessionId}/messages")
public ResponseEntity<GlobalApiResponse<ChatbotMessageHistoryResponse>> findMessages(
        @AuthenticationPrincipal AuthUser authUser,
        @PathVariable String sessionId,
        @ModelAttribute @Valid FindChatbotMessagesRequest request
) {
    ChatbotMessageHistoryResult result = messageQueryUseCase.findMessages(
            new FindChatbotMessagesQuery(authUser.userId(), sessionId, request.cursor(), request.resolveSize())
    );
    return ResponseEntity.ok(GlobalApiResponse.ok(
            ChatbotResponseCode.CHATBOT_MESSAGE_HISTORY_SUCCESS,
            ChatbotMessageHistoryResponse.from(result)
    ));
}
```

- request: `String cursor`, `@Min(1) @Max(50) Integer size`, default 20
- response code: `CHATBOT_MESSAGE_HISTORY_SUCCESS("CHATBOT_MESSAGE_HISTORY_SUCCESS", "챗봇 메시지 이력 조회에 성공했습니다.")`
- item response: `messageId`, `role`, `content`, `intentHint`, `category`, `JsonNode routine`, `JsonNode sources`, `Boolean limited`, `createdAt`
- list response: `messages`, `nextCursor`, `hasNext`

## Tests

Add MVC tests using authenticated `AuthUser(7L, ...)` for:
1. success with chronological result, JSON `routine`/`sources`, response code, query `(7L, "session-1", null, 20)`;
2. `size=51` -> `400 COMMON_400` and no interaction with message usecase;
3. default size 20.

Write tests RED then implementation GREEN.

## Docs

Update API.md: history endpoint is now implemented, with Authorization header, sessionId path, cursor/size, success JSON/fields, newest-first DB but oldest-to-newest response, size+1; errors `COMMON_400`, `AUTH_401_xxx`, `CHATBOT_SESSION_ACCESS_DENIED`, `CHATBOT_SESSION_NOT_FOUND`, `COMMON_500`.

Update API_FLOW.md with exact Controller → UseCase → Query → Service → Domain Repository → Adapter → Spring Data path, ownership validation, `(createdAt,messageId)` cursor, DB newest-first/response reversal, JSON metadata. State STOMP/FastAPI out of scope.

## Safety and verification

- Preserve `.github/workflows/deploy.yml`; never stage it.
- Include current pending chatbot docs in this Task commit.
- Do not touch migration, STOMP, FastAPI, or `ChatbotConversationService`.
- Run `ChatbotSessionControllerTest`, chatbot package tests, `compileJava`, and `git diff --check`; report exact results.
- Commit Task 4 only: `feat: expose chatbot message history api`.
- Write full report to `.superpowers/sdd/task-4-report.md`.
