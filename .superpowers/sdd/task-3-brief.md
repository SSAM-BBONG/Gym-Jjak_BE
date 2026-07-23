# Task 3 — 레이어드 챗봇 메시지 이력 Query 구현

## Scope

`GET /api/chatbot/sessions/{sessionId}/messages`가 사용할 Application Query와 Persistence Adapter를 구현한다. 이 Task에서는 Controller/HTTP Response/문서를 수정하지 않는다.

## Required files

Create:

- `src/main/java/com/ssambbong/gymjjak/chatbot/application/query/FindChatbotMessagesQuery.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryItem.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryResult.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/application/usecase/ChatbotMessageQueryUseCase.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageCursorCodec.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryService.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/exception/InvalidChatbotMessageCursorException.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotMessagePersistenceAdapter.java`
- `src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryServiceTest.java`

Modify:

- `src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/SpringDataChatbotMessageRepository.java`

## Layer rules

- `ChatbotMessageQueryService` may depend only on Domain `ChatbotSession`, `ChatbotMessage`, `ChatbotSessionRepository`, `ChatbotMessageRepository`; application types; exceptions; and `ObjectMapper`.
- It must not import Spring Data/JPA/infrastructure classes.
- `ChatbotMessagePersistenceAdapter` implements Domain `ChatbotMessageRepository`; it alone uses `SpringDataChatbotMessageRepository` and `PageRequest`.
- `ChatbotPersistenceMapper` maps `ChatbotMessageJpaEntity` to Domain `ChatbotMessage`.
- Do not modify existing `findTop12BySessionIdOrderByCreatedAtDesc`; it is used by the WebSocket/FastAPI conversation write flow.

## Required types and behavior

```java
public record FindChatbotMessagesQuery(Long userId, String sessionId, String cursor, int size) {}

public interface ChatbotMessageQueryUseCase {
    ChatbotMessageHistoryResult findMessages(FindChatbotMessagesQuery query);
}

public record ChatbotMessageHistoryItem(
        Long messageId, ChatbotMessageRole role, String content, String intentHint,
        String category, JsonNode routine, JsonNode sources, Boolean limited, LocalDateTime createdAt
) {}

public record ChatbotMessageHistoryResult(
        List<ChatbotMessageHistoryItem> messages, String nextCursor, boolean hasNext
) {}
```

Message cursor:

```java
public record CursorPayload(LocalDateTime createdAt, Long messageId) {}
```

- Encode/decode JSON with `ObjectMapper` and URL-safe Base64 without padding.
- Invalid Base64, invalid JSON, JSON `null`, null `createdAt`, or null `messageId` must throw `InvalidChatbotMessageCursorException`.
- `InvalidChatbotMessageCursorException` must extend `BadRequestException` with `CommonErrorCode.INVALID_INPUT`.

Service algorithm:

1. `sessionRepository.findBySessionId(query.sessionId())`; absent -> `new ChatbotSessionException(ChatbotErrorCode.SESSION_NOT_FOUND)`.
2. `!session.isOwnedBy(query.userId())` -> `new ChatbotSessionException(ChatbotErrorCode.SESSION_ACCESS_DENIED)`.
3. Null/blank cursor means first page; otherwise decode it.
4. `messageRepository.findHistory(sessionId, cursorCreatedAt, cursorMessageId, size + 1)` returns rows newest-first.
5. `hasNext = rows.size() > size` and retain the first `size` newest rows.
6. If `hasNext`, make next cursor from the last retained newest-first row (the oldest message in this response page).
7. Convert the retained rows to history items, then reverse them so response `messages` is oldest -> newest.
8. JSON mapping: `routineJson == null` -> `null`; `sourcesJson == null` -> `objectMapper.createArrayNode()`; otherwise `objectMapper.readTree`. Do not convert malformed stored JSON to a cursor error; allow existing unexpected-error flow.

Spring Data method:

```java
@Query("""
    SELECT m FROM ChatbotMessageJpaEntity m
    WHERE m.sessionId = :sessionId
      AND (:cursorCreatedAt IS NULL
           OR m.createdAt < :cursorCreatedAt
           OR (m.createdAt = :cursorCreatedAt AND m.id < :cursorMessageId))
    ORDER BY m.createdAt DESC, m.id DESC
    """)
List<ChatbotMessageJpaEntity> findHistory(
        @Param("sessionId") String sessionId,
        @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
        @Param("cursorMessageId") Long cursorMessageId,
        Pageable pageable
);
```

Adapter invokes it with `PageRequest.of(0, limit)`.

## TDD coverage

Write tests before production types for all cases below:

1. Three mocked newest-first domain messages at 10:02 (id 3), 10:01 (id 2), 10:00 (id 1); request size 2 -> response message IDs `[2, 3]`, `hasNext=true`, nonblank `nextCursor`, repository limit 3.
2. One owned session and one message -> `hasNext=false`, `nextCursor=null`.
3. Missing session -> `CHATBOT_SESSION_NOT_FOUND`; no message history call.
4. Session owner differs -> `CHATBOT_SESSION_ACCESS_DENIED`; no message history call.
5. `not-base64` and `bnVsbA` cursor -> `InvalidChatbotMessageCursorException`; no message history call.
6. Assistant metadata: valid `routineJson` and `sourcesJson` become JSON nodes; null sources becomes `[]`.

Run focused test RED before implementation and GREEN after.

## Safety

- Do not modify `.github/workflows/deploy.yml`.
- Do not modify `chatbot/docs/API.md`, `chatbot/docs/API_FLOW.md`, Controller, or existing session-list files.
- Do not modify STOMP, FastAPI, database migration, or `ChatbotConversationService`.
- Stage only Task 3 files.

## Commit

```powershell
git add -- src/main/java/com/ssambbong/gymjjak/chatbot/application/query/FindChatbotMessagesQuery.java src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryItem.java src/main/java/com/ssambbong/gymjjak/chatbot/application/result/ChatbotMessageHistoryResult.java src/main/java/com/ssambbong/gymjjak/chatbot/application/usecase/ChatbotMessageQueryUseCase.java src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageCursorCodec.java src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryService.java src/main/java/com/ssambbong/gymjjak/chatbot/exception/InvalidChatbotMessageCursorException.java src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotMessagePersistenceAdapter.java src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/SpringDataChatbotMessageRepository.java src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotMessageQueryServiceTest.java
git commit -m "feat: add layered chatbot message history query"
```

Write detailed report to `.superpowers/sdd/task-3-report.md`; return short status only.
