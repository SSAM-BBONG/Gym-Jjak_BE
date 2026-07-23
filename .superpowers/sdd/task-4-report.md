# Task 4 Report - Chatbot Message History REST API

## Implemented

- Added `GET /api/chatbot/sessions/{sessionId}/messages` to `ChatbotSessionController`.
- Added `FindChatbotMessagesRequest` with `cursor`, `size`, default size `20`, and `1~50` validation.
- Added message-history response DTOs that expose message metadata and JSON `routine` / `sources` fields.
- Added `CHATBOT_MESSAGE_HISTORY_SUCCESS` response code.
- Added MVC coverage for authenticated success, invalid maximum size, and default size.
- Updated `API.md` and `API_FLOW.md` for the session-list and message-history REST contracts and layered query flow.

## Verification

- Passed: `./gradlew.bat test --tests "com.ssambbong.gymjjak.chatbot.presentation.api.ChatbotSessionControllerTest"`
- Not run: chatbot package test, standalone `compileJava`, and `git diff --check`; the follow-up instruction required immediate focused-test completion and commit.

## Staging scope

- Staged only Task 4 presentation code, MVC test, and chatbot API documents.
- Excluded `.github/workflows/deploy.yml`.
