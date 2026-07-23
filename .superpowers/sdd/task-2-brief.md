# Task 2 — 기존 챗봇 세션 목록 조회를 Domain Repository 경계로 이동

## Scope

기존 `GET /api/chatbot/sessions`의 HTTP 계약을 변경하지 않고, `ChatbotSessionQueryService`가 Spring Data Repository와 infrastructure projection을 직접 참조하지 않도록 만든다.

## Prerequisite

Task 1이 `ChatbotSession`, `ChatbotSessionSummary`, `ChatbotSessionRepository`를 제공한다.

## Files

Create:

- `src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotSessionPersistenceAdapter.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java`

Modify:

- `src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryService.java`
- `src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryServiceTest.java`

## Required behavior

1. `ChatbotSessionPersistenceAdapter` implements `ChatbotSessionRepository`.
2. It delegates `findBySessionId` and `findSessionSummaries` to `SpringDataChatbotSessionRepository`.
3. `ChatbotPersistenceMapper` maps:
   - `ChatbotSessionJpaEntity` -> `ChatbotSession`
   - `ChatbotSessionListRow` -> `ChatbotSessionSummary`
4. `ChatbotSessionQueryService` injects only `ChatbotSessionRepository` and `ChatbotSessionCursorCodec`.
5. Service imports no `chatbot.infrastructure.persistence` type.
6. Existing cursor behavior stays identical: null/blank means first page; nonblank invalid input throws `InvalidChatbotSessionCursorException`; query limit is `size + 1`; next cursor comes from last returned session only when hasNext.
7. Existing Controller and documentation files are out of scope.

## TDD

Update `ChatbotSessionQueryServiceTest` first:

- mock `ChatbotSessionRepository`, not `SpringDataChatbotSessionRepository`
- use real `ChatbotSessionSummary` instead of `ChatbotSessionListRow`
- verify `findSessionSummaries(7L, null, null, 21)` on initial page
- retain malformed Base64 and JSON-null cursor tests with no repository interaction

Run the focused test once RED, implement adapter/service, then run it GREEN. Also run existing `ChatbotSessionControllerTest` as a compatibility check.

## Safety

- Do not modify `.github/workflows/deploy.yml`.
- Do not modify `chatbot/docs/API.md` or `chatbot/docs/API_FLOW.md`.
- Do not modify WebSocket/FastAPI/conversation write code.
- Do not stage files outside Task 2.

## Commit

```powershell
git add -- src/main/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryService.java src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotSessionPersistenceAdapter.java src/main/java/com/ssambbong/gymjjak/chatbot/infrastructure/persistence/ChatbotPersistenceMapper.java src/test/java/com/ssambbong/gymjjak/chatbot/application/service/ChatbotSessionQueryServiceTest.java
git commit -m "refactor: route chatbot session query through domain repository"
```

Write detailed changed files, RED/GREEN test results, self-review, and commit SHA to `.superpowers/sdd/task-2-report.md`. Return short status only.
