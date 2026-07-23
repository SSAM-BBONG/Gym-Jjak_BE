# Final Review Package: Chatbot Read Query Refactoring

## Scope

- Base commit: `bb469857`
- Review target: current `HEAD`
- Exclude the user's unrelated working-tree change: `.github/workflows/deploy.yml`

## User story

Authenticated users can retrieve:

1. their prior chatbot session list; and
2. the message history for one of their own sessions.

Both read paths must use the trainerprofile-style layered architecture:

`Controller -> UseCase -> Query -> Service -> domain repository interface -> persistence adapter -> Spring Data repository`.

## Required behavior

- `GET /api/chatbot/sessions`: first user question preview, cursor pagination.
- `GET /api/chatbot/sessions/{sessionId}/messages`: ownership validation before history lookup, `size + 1` cursor pagination, storage newest-first and response oldest-to-newest.
- Missing session returns 404; another user's session returns 403.
- Empty owned session returns an empty successful history page.
- Do not alter the STOMP/FastAPI write path or database schema.

## Evidence already verified

- `./gradlew.bat test --tests "com.ssambbong.gymjjak.chatbot.*"` succeeded.
- `./gradlew.bat compileJava` succeeded.
- `git diff --check bb469857..HEAD` produced no output.

## Reviewer instructions

Inspect `git diff bb469857..HEAD` and the relevant chatbot code/tests/docs. Report only actionable correctness, security, layering, pagination, API-contract, or test-coverage issues. Finish with exactly one line: `Final review: approved` or `Final review: reject`.
