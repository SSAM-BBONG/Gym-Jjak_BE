# Task 1 — 챗봇 조회 Domain Model과 Repository 경계 생성

## Scope

Trainerprofile과 같은 레이어드 구조를 위한 읽기 전용 Domain Model 및 Domain Repository 인터페이스를 추가한다. 이 Task에서는 기존 서비스, JPA Entity, Spring Data Repository, Controller, 문서를 변경하지 않는다.

## Files

Create:

- `src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSession.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionSummary.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotMessage.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository/ChatbotSessionRepository.java`
- `src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository/ChatbotMessageRepository.java`
- `src/test/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionTest.java`

## Required interfaces

```java
public record ChatbotSession(String sessionId, Long userId, LocalDateTime lastActivityAt) {
    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}

public record ChatbotSessionSummary(
        String sessionId, String title, String lastMessage, LocalDateTime lastActivityAt
) {}

public record ChatbotMessage(
        Long messageId, String sessionId, ChatbotMessageRole role, String content,
        String intentHint, String category, String routineJson, String sourcesJson,
        Boolean limited, LocalDateTime createdAt
) {}
```

```java
public interface ChatbotSessionRepository {
    Optional<ChatbotSession> findBySessionId(String sessionId);
    List<ChatbotSessionSummary> findSessionSummaries(
            Long userId, LocalDateTime cursorLastActivityAt, String cursorSessionId, int limit
    );
}

public interface ChatbotMessageRepository {
    List<ChatbotMessage> findHistory(
            String sessionId, LocalDateTime cursorCreatedAt, Long cursorMessageId, int limit
    );
}
```

## TDD

Write `ChatbotSessionTest.returnsTrueOnlyForItsOwner()` before production code. It must construct a session with user ID 7 and assert `isOwnedBy(7L)` is true, `isOwnedBy(8L)` is false. Run it once RED, add only the required types, then run it GREEN.

## Safety

- Do not touch `.github/workflows/deploy.yml`.
- Do not touch `chatbot/docs/API.md` or `chatbot/docs/API_FLOW.md`.
- Do not change STOMP, FastAPI, JPA Entities, Spring Data Repositories, existing query services, or controllers.

## Commit

Stage only Task 1 files and commit:

```powershell
git add -- src/main/java/com/ssambbong/gymjjak/chatbot/domain/model src/main/java/com/ssambbong/gymjjak/chatbot/domain/repository src/test/java/com/ssambbong/gymjjak/chatbot/domain/model/ChatbotSessionTest.java
git commit -m "refactor: add chatbot query domain boundaries"
```

Write detailed changed files, RED/GREEN command results, self-review, and commit SHA to `.superpowers/sdd/task-1-report.md`. Return only `DONE`/`DONE_WITH_CONCERNS`/`BLOCKED`, commit SHA, one test summary, and concerns.
