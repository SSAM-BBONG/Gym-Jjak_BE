# Task 3 Report - Layered Chatbot Message History Query

## Implemented

- Added `FindChatbotMessagesQuery`, `ChatbotMessageQueryUseCase`, and message-history result records.
- Added `ChatbotMessageQueryService` with session existence/ownership checks, newest-first `size + 1` retrieval, an oldest-to-newest response, and a next cursor based on the oldest returned message.
- Added URL-safe Base64 JSON cursor encoding/decoding and `InvalidChatbotMessageCursorException` for invalid cursor inputs.
- Added `ChatbotMessagePersistenceAdapter` so the application service depends on domain repositories only.
- Added the Spring Data `findHistory` query with `(createdAt DESC, id DESC)` ordering and cursor predicates, without changing the existing FastAPI/WebSocket `findTop12BySessionIdOrderByCreatedAtDesc` method.
- Added mapper support for `ChatbotMessageJpaEntity -> ChatbotMessage`.

## TDD Evidence

- RED: `ChatbotMessageQueryServiceTest` initially failed during test compilation because the required query/result/service/exception types did not exist.
- GREEN: `./gradlew.bat test --tests "com.ssambbong.gymjjak.chatbot.application.service.ChatbotMessageQueryServiceTest"` passed.

## Coverage

- `size + 1`, next cursor, and oldest-to-newest response ordering.
- Last-page response without a cursor.
- Missing session and non-owner access paths without message history lookup.
- Invalid Base64 and JSON-null cursors without message history lookup.
- Assistant JSON metadata mapping and empty `sources` array for null storage value.

## Self Review

- Application service imports only application/domain/exception/ObjectMapper dependencies; no Spring Data, JPA, or infrastructure dependency exists there.
- Persistence-specific `PageRequest` and Spring Data calls are confined to `ChatbotMessagePersistenceAdapter`.
- Existing docs, deploy workflow, controller, WebSocket/FastAPI flow, migration, and conversation service were not modified.
- `git diff --check` passed before staging.

## Final Commit

- Commit: `2488c74a feat: add layered chatbot message history query`
- Final focused test: `./gradlew.bat test --tests "com.ssambbong.gymjjak.chatbot.application.service.ChatbotMessageQueryServiceTest"` — `BUILD SUCCESSFUL`.

## Empty History Regression Fix

- Root cause: `ChatbotMessageQueryService` read `retainedRows.get(retainedRows.size() - 1)` even when the owned session had no stored messages.
- RED: `returnsEmptyHistoryWithoutNextCursorWhenOwnedSessionHasNoMessages` failed with `IndexOutOfBoundsException` at `ChatbotMessageQueryServiceTest.java:90`.
- Fix: compute the cursor's last-item values only on the `hasNext` branch.
- GREEN: `./gradlew.bat test --tests "com.ssambbong.gymjjak.chatbot.application.service.ChatbotMessageQueryServiceTest"` — `BUILD SUCCESSFUL`.
- Commit: `9a5e9b43 fix: handle empty chatbot message history` (Service and regression test only).
