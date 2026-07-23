08b4b950 feat: expose chatbot message history api
 .../java/com/ssambbong/gymjjak/chatbot/docs/API.md | 146 ++++++++++++++-------
 .../com/ssambbong/gymjjak/chatbot/docs/API_FLOW.md |  80 +++++++++++
 .../presentation/api/ChatbotSessionController.java |  22 ++++
 .../api/request/FindChatbotMessagesRequest.java    |  15 +++
 .../ChatbotMessageHistoryItemResponse.java         |  33 +++++
 .../response/ChatbotMessageHistoryResponse.java    |  21 +++
 .../api/response/ChatbotResponseCode.java          |   4 +
 .../api/ChatbotSessionControllerTest.java          |  67 ++++++++++
 8 files changed, 341 insertions(+), 47 deletions(-)
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/docs/API.md b/src/main/java/com/ssambbong/gymjjak/chatbot/docs/API.md
index 1f7bd31f..27b7b3e1 100644
--- a/src/main/java/com/ssambbong/gymjjak/chatbot/docs/API.md
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/docs/API.md
@@ -1,110 +1,162 @@
-# 📚 챗봇 REST API
+# 챗봇 REST API
 
 - 작성일: 2026-07-23
-- 최종 수정일: 2026-07-23
-- 기본 경로: `/api/chatbot/sessions`
+- 기준 경로: `/api/chatbot/sessions`
 
-> 메시지 생성은 REST가 아닌 [WEBSOCKET_API.md](WEBSOCKET_API.md)의 STOMP API를 사용합니다. 아래 API는 저장된 챗봇 세션과 이력 조회 전용입니다.
->
-> `GET /api/chatbot/sessions`는 구현 완료되었습니다. 메시지 이력 조회 API는 구현 예정입니다.
+> 챗봇 메시지 생성과 스트리밍은 REST가 아니라 [WEBSOCKET_API.md](WEBSOCKET_API.md)의 STOMP API를 사용한다.
 
-## 📋 세션 목록 조회
+---
+
+## 1. 챗봇 세션 목록 조회
+
+로그인 사용자의 이전 챗봇 세션을 최근 활동 순으로 조회한다.
 
 `GET /api/chatbot/sessions?cursor={cursor}&size={size}`
 
-로그인한 사용자의 세션만 `lastActivityAt DESC, sessionId DESC` 순으로 조회합니다.
+### Request Header
+
+| name | required | description |
+| --- | --- | --- |
+| `Authorization` | Y | `Bearer {accessToken}` |
+
+### Query Parameter
 
-| Query parameter | 필수 여부 | 설명 |
+| name | required | description |
 | --- | --- | --- |
-| `cursor` | 선택 | 다음 페이지 조회용 커서입니다. `(lastActivityAt, sessionId)`를 인코딩한 불투명 값입니다. |
-| `size` | 선택 | 조회 개수입니다. 기본값은 `20`이고, 허용 범위는 `1~50`입니다. |
+| `cursor` | N | 이전 응답의 `nextCursor`. 없거나 공백이면 첫 페이지를 조회한다. |
+| `size` | N | 조회 개수. 기본값 `20`, 허용 범위 `1~50` |
+
+### Response
 
-- 서버는 `size + 1`개를 조회하여 다음 페이지 존재 여부를 판단합니다.
-- `hasNext`가 `true`이면 `nextCursor`를 다음 요청의 `cursor`로 전달합니다. 마지막 페이지면 `hasNext`는 `false`, `nextCursor`는 `null`입니다.
-- `sessionId`는 외부에 노출하는 UUID이며, DB 내부 PK인 `chatbot_session_id`는 응답하지 않습니다.
-- `title`은 세션의 첫 번째 `USER` 메시지이고, `lastMessage`는 해당 세션의 최신 메시지입니다.
+| HTTP 상태 | code | message |
+| --- | --- | --- |
+| `200 OK` | `CHATBOT_SESSION_LIST_SUCCESS` | 챗봇 세션 목록 조회에 성공했습니다. |
 
 ```json
 {
   "status": 200,
   "code": "CHATBOT_SESSION_LIST_SUCCESS",
   "message": "챗봇 세션 목록 조회에 성공했습니다.",
   "data": {
     "sessions": [
       {
         "sessionId": "019f0000-0000-7000-8000-000000000001",
-        "title": "하체 루틴 상담",
-        "lastMessage": "이번 주는 하체를 두 번 나누어 진행해 보세요.",
+        "title": "하체 운동 루틴 상담",
+        "lastMessage": "이번 주는 하체를 주 2회로 나누어 진행해 보세요.",
         "lastActivityAt": "2026-07-23T10:00:00"
       }
     ],
-    "nextCursor": null,
-    "hasNext": false
+    "nextCursor": "eyJsYXN0QWN0aXZpdHlBdCI6IjIwMjYtMDctMjNUMTA6MDA6MDAiLCJzZXNzaW9uSWQiOiIwMTlmMDAwMC0wMDAwLTcwMDAtODAwMC0wMDAwMDAwMDAwMDEifQ",
+    "hasNext": true
   }
 }
 ```
 
-## 💬 메시지 이력 조회
+| field | description |
+| --- | --- |
+| `data.sessions[].sessionId` | 외부에 노출하는 세션 UUID. DB PK `chatbot_session_id`는 노출하지 않는다. |
+| `data.sessions[].title` | 세션의 첫 번째 `USER` 메시지 내용 |
+| `data.sessions[].lastMessage` | 세션의 최신 메시지 내용 (`USER`, `ASSISTANT` 포함) |
+| `data.sessions[].lastActivityAt` | 세션 마지막 활동 시각. ISO-8601 `LocalDateTime` |
+| `data.nextCursor` | 다음 페이지 cursor. 마지막 페이지면 `null` |
+| `data.hasNext` | 다음 페이지 존재 여부 |
+
+정렬은 `lastActivityAt DESC, sessionId DESC`이며, 서버는 `size + 1`개를 읽어 다음 페이지 존재 여부를 판단한다.
 
-> **구현 예정**
+---
+
+## 2. 챗봇 메시지 이력 조회
+
+로그인 사용자가 소유한 세션의 메시지 이력을 조회한다. DB에서는 최신 메시지부터 조회하지만, 프론트가 바로 렌더링하도록 응답 배열은 오래된 메시지부터 최신 메시지 순으로 반환한다. 이전 대화는 화면을 위로 스크롤할 때 `nextCursor`로 추가 조회한다.
 
 `GET /api/chatbot/sessions/{sessionId}/messages?cursor={cursor}&size={size}`
 
-세션 소유자만 조회할 수 있습니다. 결과는 화면 재현을 위해 오래된 메시지부터 반환하며, `cursor`는 `(createdAt, messageId)`를 인코딩한 불투명 값입니다. 서버는 페이지 경계의 안정성을 위해 같은 정렬 키를 함께 사용합니다.
+### Request Header
+
+| name | required | description |
+| --- | --- | --- |
+| `Authorization` | Y | `Bearer {accessToken}` |
+
+### Path / Query Parameter
+
+| name | location | required | description |
+| --- | --- | --- | --- |
+| `sessionId` | path | Y | 조회할 챗봇 세션 UUID |
+| `cursor` | query | N | 이전 응답의 `nextCursor`. 없거나 공백이면 최신 메시지부터 첫 페이지를 조회한다. |
+| `size` | query | N | 조회 개수. 기본값 `20`, 허용 범위 `1~50` |
+
+### Response
+
+| HTTP 상태 | code | message |
+| --- | --- | --- |
+| `200 OK` | `CHATBOT_MESSAGE_HISTORY_SUCCESS` | 챗봇 메시지 이력 조회에 성공했습니다. |
 
 ```json
 {
   "status": 200,
-  "code": "CHATBOT_MESSAGE_LIST_SUCCESS",
+  "code": "CHATBOT_MESSAGE_HISTORY_SUCCESS",
   "message": "챗봇 메시지 이력 조회에 성공했습니다.",
   "data": {
-    "sessionId": "019f0000-0000-7000-8000-000000000001",
     "messages": [
       {
         "messageId": 101,
         "role": "USER",
-        "content": "이번 주 하체 운동 루틴을 추천해줘.",
-        "intentHint": "ROUTINE_RECOMMENDATION",
+        "content": "하체 운동 루틴을 추천해줘",
+        "intentHint": null,
         "category": null,
         "routine": null,
         "sources": [],
-        "createdAt": "2026-07-23T10:00:00"
+        "limited": null,
+        "createdAt": "2026-07-23T09:58:00"
       },
       {
         "messageId": 102,
         "role": "ASSISTANT",
-        "content": "이번 주는 하체를 두 번 나누어 진행해 보세요.",
-        "intentHint": null,
+        "content": "주 2회 하체 루틴을 제안할게요.",
+        "intentHint": "ROUTINE",
         "category": "ROUTINE",
-        "routine": null,
-        "sources": [],
+        "routine": { "name": "하체 루틴" },
+        "sources": [{ "title": "운동 가이드" }],
         "limited": false,
-        "createdAt": "2026-07-23T10:00:04"
+        "createdAt": "2026-07-23T09:59:00"
       }
     ],
-    "nextCursor": null,
-    "hasNext": false
+    "nextCursor": "eyJjcmVhdGVkQXQiOiIyMDI2LTA3LTIzVDA5OjU4OjAwIiwibWVzc2FnZUlkIjoxMDF9",
+    "hasNext": true
   }
 }
 ```
 
-## ⚠️ 공통 오류
+| field | description |
+| --- | --- |
+| `data.messages[].messageId` | 메시지 식별자 |
+| `data.messages[].role` | 발신자 역할: `USER`, `ASSISTANT` |
+| `data.messages[].content` | 메시지 본문 |
+| `data.messages[].intentHint` | assistant 응답의 의도 힌트. 없으면 `null` |
+| `data.messages[].category` | assistant 응답 카테고리. 없으면 `null` |
+| `data.messages[].routine` | assistant 루틴 JSON. 없으면 `null` |
+| `data.messages[].sources` | assistant 출처 JSON 배열. 출처가 없으면 `[]` |
+| `data.messages[].limited` | 응답 제한 여부. 없으면 `null` |
+| `data.messages[].createdAt` | 메시지 생성 시각. ISO-8601 `LocalDateTime` |
+| `data.nextCursor` | 더 과거 메시지를 조회하는 cursor. 더 없으면 `null` |
+| `data.hasNext` | 더 과거 메시지 존재 여부 |
+
+페이지네이션 기준은 `createdAt DESC, messageId DESC`이다. 서버는 최신순으로 `size + 1`개를 조회한 후 응답 대상만 오래된 순으로 뒤집는다. `hasNext=true`일 때 `nextCursor`는 이번 응답의 가장 오래된 메시지를 기준으로 생성한다. 메시지가 없는 정상 세션은 `messages: []`, `hasNext: false`, `nextCursor: null`을 반환한다.
+
+### Error Response
 
 | HTTP 상태 | code | 발생 조건 |
 | --- | --- | --- |
-| 400 | `COMMON_400` | 잘못된 cursor 또는 size 범위 위반 |
-| 401 | `COMMON_401` | 인증되지 않은 요청 |
-| 403 | `COMMON_403` | 다른 사용자의 세션 접근 |
-| 404 | `CHATBOT_SESSION_NOT_FOUND` | 존재하지 않거나 정리된 세션 |
-
-## 🧹 보관 정책
+| `400 Bad Request` | `COMMON_400` | `size`가 `1~50` 범위를 벗어남, 숫자 변환 실패, 잘못된 cursor(Base64/JSON/필수 값 누락) |
+| `401 Unauthorized` | `AUTH_401_xxx` | Access Token 누락·만료·위조 또는 claim 검증 실패 |
+| `403 Forbidden` | `CHATBOT_SESSION_ACCESS_DENIED` | 다른 사용자의 세션 이력 조회 시도 |
+| `404 Not Found` | `CHATBOT_SESSION_NOT_FOUND` | 존재하지 않는 세션 조회 |
+| `500 Internal Server Error` | `COMMON_500` | 예상하지 못한 서버 오류 |
 
-- 마지막 활동 후 6개월이 지난 비활성 세션은 정리 배치에서 종속 메시지와 함께 삭제합니다.
-- 정리 완료 세션은 목록과 이력 조회에서 `CHATBOT_SESSION_NOT_FOUND`로 처리합니다.
-- 메시지 생성 중에는 STOMP의 `started/delta/done/error` 이벤트를 사용하며, `error`로 끝난 assistant 응답은 이력에 남지 않습니다.
+---
 
-## 📝 변경 이력
+## 문서 변경 이력
 
-| 날짜 | 변경 내용 |
+| 날짜 | 내용 |
 | --- | --- |
-| 2026-07-23 | 챗봇 세션 목록 조회 API 구현 계약 반영, 메시지 이력 조회는 구현 예정으로 유지 |
+| 2026-07-23 | 세션 목록 조회 API와 메시지 이력 조회 API 계약을 구현 기준으로 최신화 |
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/docs/API_FLOW.md b/src/main/java/com/ssambbong/gymjjak/chatbot/docs/API_FLOW.md
new file mode 100644
index 00000000..d81334a0
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/docs/API_FLOW.md
@@ -0,0 +1,80 @@
+# 챗봇 REST API Flow
+
+이 문서는 REST 조회 API의 호출 흐름을 정리한다. 메시지 생성, FastAPI 호출, STOMP 스트리밍은 이 범위에 포함하지 않으며 [WEBSOCKET_FLOW.md](WEBSOCKET_FLOW.md)를 따른다.
+
+---
+
+## 1. 공통 레이어 구조
+
+```text
+Controller
+→ UseCase 인터페이스
+→ Query
+→ Service
+→ Domain Repository 인터페이스
+→ Persistence Adapter
+→ Spring Data Repository
+```
+
+Controller는 HTTP 요청·응답만 처리하고, Service는 JPA Entity나 Spring Data 타입을 직접 참조하지 않는다. Domain Repository는 application/domain과 infrastructure를 분리하며, Adapter가 Spring Data 조회 결과를 Domain Model로 변환한다.
+
+JWT 인증은 `SecurityConfig`와 JWT 필터가 처리한다. 인증이 성공하면 `@AuthenticationPrincipal AuthUser`로 사용자 ID를 전달하며, 요청에서 `userId`를 받지 않으므로 타 사용자 ID를 직접 지정할 수 없다.
+
+---
+
+## 2. 세션 목록 조회 Flow
+
+`GET /api/chatbot/sessions?cursor={cursor}&size={size}`
+
+```text
+ChatbotSessionController.findSessions()
+→ ChatbotSessionQueryUseCase.findSessions(FindChatbotSessionsQuery)
+→ ChatbotSessionQueryService.findSessions()
+→ ChatbotSessionRepository.findSessionSummaries()
+→ ChatbotSessionPersistenceAdapter
+→ SpringDataChatbotSessionRepository.findSessionList()
+```
+
+1. `FindChatbotSessionsRequest`가 `cursor`, `size`를 바인딩하고 `size` 범위를 `1~50`으로 검증한다. 기본값은 `20`이다.
+2. Controller가 인증 사용자 ID와 요청 값을 `FindChatbotSessionsQuery`로 만들어 UseCase에 전달한다.
+3. Service가 `(lastActivityAt, sessionId)` cursor를 해석한다. 잘못된 Base64, JSON, JSON `null`, 필수 값 누락은 `InvalidChatbotSessionCursorException`으로 변환되어 `COMMON_400`이 된다.
+4. Domain Repository는 `size + 1`개를 조회한다. Adapter가 Spring Data native query 결과를 `ChatbotSessionSummary`로 매핑한다.
+5. Spring Data query는 사용자 소유 세션만 대상으로 첫 `USER` 메시지와 최신 메시지를 함께 읽는다. 정렬은 `lastActivityAt DESC, sessionId DESC`이며 목록 조회 중 N+1이 발생하지 않는다.
+6. Service가 `hasNext`, `nextCursor`를 계산하고 `ChatbotSessionListResult`를 반환한다.
+7. `ChatbotSessionListResponse`가 HTTP 응답 DTO로 변환되고 `GlobalApiResponse`로 감싸져 반환된다.
+
+---
+
+## 3. 메시지 이력 조회 Flow
+
+`GET /api/chatbot/sessions/{sessionId}/messages?cursor={cursor}&size={size}`
+
+```text
+ChatbotSessionController.findMessages()
+→ ChatbotMessageQueryUseCase.findMessages(FindChatbotMessagesQuery)
+→ ChatbotMessageQueryService.findMessages()
+→ ChatbotSessionRepository.findBySessionId()
+→ ChatbotMessageRepository.findHistory()
+→ ChatbotSessionPersistenceAdapter / ChatbotMessagePersistenceAdapter
+→ SpringDataChatbotSessionRepository / SpringDataChatbotMessageRepository
+```
+
+1. `FindChatbotMessagesRequest`가 `cursor`, `size`를 바인딩하고 `size` 범위를 `1~50`으로 검증한다. 기본값은 `20`이다.
+2. Controller가 `AuthUser.userId()`, path의 `sessionId`, 요청값으로 `FindChatbotMessagesQuery`를 생성한다.
+3. Service가 먼저 `ChatbotSessionRepository.findBySessionId()`로 세션을 조회한다.
+   - 세션이 없으면 `CHATBOT_SESSION_NOT_FOUND`(404)를 반환한다.
+   - 세션의 `isOwnedBy(userId)`가 false이면 `CHATBOT_SESSION_ACCESS_DENIED`(403)를 반환한다.
+4. `(createdAt, messageId)` cursor를 해석한다. 잘못된 cursor는 `InvalidChatbotMessageCursorException`을 통해 `COMMON_400`으로 반환한다.
+5. `ChatbotMessageRepository.findHistory()`가 최신순 `createdAt DESC, messageId DESC`으로 `size + 1`개를 조회한다. Persistence Adapter는 JPA Entity를 `ChatbotMessage` Domain Model로 변환한다.
+6. Service는 조회 대상 `size`개를 오래된 순으로 뒤집어 `ChatbotMessageHistoryResult`로 반환한다. 더 과거 메시지가 있으면 응답의 가장 오래된 메시지를 기준으로 `nextCursor`를 만든다.
+7. `routineJson`, `sourcesJson`은 Service에서 JSON으로 파싱해 `routine`, `sources`로 전달한다. `sourcesJson`이 없으면 빈 JSON 배열을 반환한다.
+8. `ChatbotMessageHistoryResponse`가 HTTP DTO로 변환되고 `GlobalApiResponse`로 감싸져 반환된다. 빈 세션 이력은 빈 목록과 `hasNext=false`, `nextCursor=null`이다.
+
+---
+
+## 4. 범위 제외
+
+- STOMP `started`, `delta`, `done`, `error` 이벤트 처리
+- FastAPI 챗봇 응답 스트리밍 및 재시도
+- 챗봇 메시지 저장을 담당하는 `ChatbotConversationService`
+- DB migration 변경
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionController.java b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionController.java
index 234bbd76..bc685a96 100644
--- a/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionController.java
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionController.java
@@ -1,40 +1,62 @@
 package com.ssambbong.gymjjak.chatbot.presentation.api;
 
+import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
 import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
 import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
+import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotMessageQueryUseCase;
 import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotSessionQueryUseCase;
+import com.ssambbong.gymjjak.chatbot.presentation.api.request.FindChatbotMessagesRequest;
 import com.ssambbong.gymjjak.chatbot.presentation.api.request.FindChatbotSessionsRequest;
 import com.ssambbong.gymjjak.chatbot.presentation.api.response.ChatbotResponseCode;
+import com.ssambbong.gymjjak.chatbot.presentation.api.response.ChatbotMessageHistoryResponse;
 import com.ssambbong.gymjjak.chatbot.presentation.api.response.ChatbotSessionListResponse;
 import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
 import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
 import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.ModelAttribute;
+import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;
 
 @RestController
 @RequestMapping("/api/chatbot/sessions")
 @RequiredArgsConstructor
 public class ChatbotSessionController {
 
     private final ChatbotSessionQueryUseCase queryUseCase;
+    private final ChatbotMessageQueryUseCase messageQueryUseCase;
 
     @GetMapping
     public ResponseEntity<GlobalApiResponse<ChatbotSessionListResponse>> findSessions(
             @AuthenticationPrincipal AuthUser authUser,
             @ModelAttribute @Valid FindChatbotSessionsRequest request
     ) {
         ChatbotSessionListResult result = queryUseCase.findSessions(
                 new FindChatbotSessionsQuery(authUser.userId(), request.cursor(), request.resolveSize())
         );
         return ResponseEntity.ok(GlobalApiResponse.ok(
                 ChatbotResponseCode.CHATBOT_SESSION_LIST_SUCCESS,
                 ChatbotSessionListResponse.from(result)
         ));
     }
+
+    @GetMapping("/{sessionId}/messages")
+    public ResponseEntity<GlobalApiResponse<ChatbotMessageHistoryResponse>> findMessages(
+            @AuthenticationPrincipal AuthUser authUser,
+            @PathVariable String sessionId,
+            @ModelAttribute @Valid FindChatbotMessagesRequest request
+    ) {
+        ChatbotMessageHistoryResult result = messageQueryUseCase.findMessages(
+                new FindChatbotMessagesQuery(authUser.userId(), sessionId, request.cursor(), request.resolveSize())
+        );
+        return ResponseEntity.ok(GlobalApiResponse.ok(
+                ChatbotResponseCode.CHATBOT_MESSAGE_HISTORY_SUCCESS,
+                ChatbotMessageHistoryResponse.from(result)
+        ));
+    }
 }
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/request/FindChatbotMessagesRequest.java b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/request/FindChatbotMessagesRequest.java
new file mode 100644
index 00000000..6e8ddd98
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/request/FindChatbotMessagesRequest.java
@@ -0,0 +1,15 @@
+package com.ssambbong.gymjjak.chatbot.presentation.api.request;
+
+import jakarta.validation.constraints.Max;
+import jakarta.validation.constraints.Min;
+
+public record FindChatbotMessagesRequest(
+        String cursor,
+        @Min(1) @Max(50) Integer size
+) {
+    private static final int DEFAULT_SIZE = 20;
+
+    public int resolveSize() {
+        return size == null ? DEFAULT_SIZE : size;
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotMessageHistoryItemResponse.java b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotMessageHistoryItemResponse.java
new file mode 100644
index 00000000..829f8ea0
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotMessageHistoryItemResponse.java
@@ -0,0 +1,33 @@
+package com.ssambbong.gymjjak.chatbot.presentation.api.response;
+
+import com.fasterxml.jackson.databind.JsonNode;
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryItem;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;
+
+import java.time.LocalDateTime;
+
+public record ChatbotMessageHistoryItemResponse(
+        Long messageId,
+        ChatbotMessageRole role,
+        String content,
+        String intentHint,
+        String category,
+        JsonNode routine,
+        JsonNode sources,
+        Boolean limited,
+        LocalDateTime createdAt
+) {
+    public static ChatbotMessageHistoryItemResponse from(ChatbotMessageHistoryItem item) {
+        return new ChatbotMessageHistoryItemResponse(
+                item.messageId(),
+                item.role(),
+                item.content(),
+                item.intentHint(),
+                item.category(),
+                item.routine(),
+                item.sources(),
+                item.limited(),
+                item.createdAt()
+        );
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotMessageHistoryResponse.java b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotMessageHistoryResponse.java
new file mode 100644
index 00000000..c443b7c1
--- /dev/null
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotMessageHistoryResponse.java
@@ -0,0 +1,21 @@
+package com.ssambbong.gymjjak.chatbot.presentation.api.response;
+
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
+
+import java.util.List;
+
+public record ChatbotMessageHistoryResponse(
+        List<ChatbotMessageHistoryItemResponse> messages,
+        String nextCursor,
+        boolean hasNext
+) {
+    public static ChatbotMessageHistoryResponse from(ChatbotMessageHistoryResult result) {
+        return new ChatbotMessageHistoryResponse(
+                result.messages().stream()
+                        .map(ChatbotMessageHistoryItemResponse::from)
+                        .toList(),
+                result.nextCursor(),
+                result.hasNext()
+        );
+    }
+}
diff --git a/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotResponseCode.java b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotResponseCode.java
index f6e88020..a02598af 100644
--- a/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotResponseCode.java
+++ b/src/main/java/com/ssambbong/gymjjak/chatbot/presentation/api/response/ChatbotResponseCode.java
@@ -4,15 +4,19 @@ import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
 import lombok.Getter;
 import lombok.RequiredArgsConstructor;
 
 @Getter
 @RequiredArgsConstructor
 public enum ChatbotResponseCode implements ResponseCode {
 
     CHATBOT_SESSION_LIST_SUCCESS(
             "CHATBOT_SESSION_LIST_SUCCESS",
             "챗봇 세션 목록 조회에 성공했습니다."
+    ),
+    CHATBOT_MESSAGE_HISTORY_SUCCESS(
+            "CHATBOT_MESSAGE_HISTORY_SUCCESS",
+            "챗봇 메시지 이력 조회에 성공했습니다."
     );
 
     private final String code;
     private final String message;
 }
diff --git a/src/test/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionControllerTest.java b/src/test/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionControllerTest.java
index c7ecd780..aaabf7d2 100644
--- a/src/test/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionControllerTest.java
+++ b/src/test/java/com/ssambbong/gymjjak/chatbot/presentation/api/ChatbotSessionControllerTest.java
@@ -1,16 +1,22 @@
 package com.ssambbong.gymjjak.chatbot.presentation.api;
 
+import com.fasterxml.jackson.databind.ObjectMapper;
+import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
 import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryItem;
+import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
 import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListItem;
 import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
+import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotMessageQueryUseCase;
 import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotSessionQueryUseCase;
+import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;
 import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
 import com.ssambbong.gymjjak.global.infrastructure.config.AiServiceProperties;
 import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
 import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
 import org.junit.jupiter.api.Test;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
 import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 import org.springframework.security.core.Authentication;
 import org.springframework.security.core.authority.SimpleGrantedAuthority;
@@ -31,20 +37,23 @@ import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.
 
 @WebMvcTest(ChatbotSessionController.class)
 class ChatbotSessionControllerTest {
 
     @Autowired
     private MockMvc mockMvc;
 
     @MockitoBean
     private ChatbotSessionQueryUseCase queryUseCase;
 
+    @MockitoBean
+    private ChatbotMessageQueryUseCase messageQueryUseCase;
+
     @MockitoBean
     private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
 
     @MockitoBean
     private JwtAuthenticationConverter jwtAuthenticationConverter;
 
     @MockitoBean
     private AiServiceProperties aiServiceProperties;
 
     @Test
@@ -84,19 +93,77 @@ class ChatbotSessionControllerTest {
     @Test
     void findSessions_usesDefaultSizeWhenSizeIsOmitted() throws Exception {
         when(queryUseCase.findSessions(any())).thenReturn(new ChatbotSessionListResult(List.of(), null, false));
 
         mockMvc.perform(get("/api/chatbot/sessions").with(authentication(userAuthentication())))
                 .andExpect(status().isOk());
 
         verify(queryUseCase).findSessions(new FindChatbotSessionsQuery(7L, null, 20));
     }
 
+    @Test
+    void findMessages_returnsChronologicalHistoryForAuthenticatedUser() throws Exception {
+        ObjectMapper objectMapper = new ObjectMapper();
+        when(messageQueryUseCase.findMessages(any())).thenReturn(new ChatbotMessageHistoryResult(
+                List.of(
+                        new ChatbotMessageHistoryItem(
+                                10L, ChatbotMessageRole.USER, "첫 질문", null, null,
+                                null, objectMapper.readTree("[]"), null,
+                                LocalDateTime.of(2026, 7, 23, 10, 0)
+                        ),
+                        new ChatbotMessageHistoryItem(
+                                11L, ChatbotMessageRole.ASSISTANT, "답변", "ROUTINE", "ROUTINE",
+                                objectMapper.readTree("{\"name\":\"상체 운동\"}"),
+                                objectMapper.readTree("[{\"title\":\"출처\"}]"), false,
+                                LocalDateTime.of(2026, 7, 23, 10, 1)
+                        )
+                ),
+                "previous-cursor",
+                true
+        ));
+
+        mockMvc.perform(get("/api/chatbot/sessions/session-1/messages")
+                        .with(authentication(userAuthentication())))
+                .andExpect(status().isOk())
+                .andExpect(jsonPath("$.status").value(200))
+                .andExpect(jsonPath("$.code").value("CHATBOT_MESSAGE_HISTORY_SUCCESS"))
+                .andExpect(jsonPath("$.data.messages[0].messageId").value(10))
+                .andExpect(jsonPath("$.data.messages[0].role").value("USER"))
+                .andExpect(jsonPath("$.data.messages[1].routine.name").value("상체 운동"))
+                .andExpect(jsonPath("$.data.messages[1].sources[0].title").value("출처"))
+                .andExpect(jsonPath("$.data.nextCursor").value("previous-cursor"))
+                .andExpect(jsonPath("$.data.hasNext").value(true));
+
+        verify(messageQueryUseCase).findMessages(new FindChatbotMessagesQuery(7L, "session-1", null, 20));
+    }
+
+    @Test
+    void findMessages_returnsBadRequestWhenSizeExceedsMaximum() throws Exception {
+        mockMvc.perform(get("/api/chatbot/sessions/session-1/messages")
+                        .param("size", "51")
+                        .with(authentication(userAuthentication())))
+                .andExpect(status().isBadRequest())
+                .andExpect(jsonPath("$.code").value("COMMON_400"));
+
+        verifyNoInteractions(messageQueryUseCase);
+    }
+
+    @Test
+    void findMessages_usesDefaultSizeWhenSizeIsOmitted() throws Exception {
+        when(messageQueryUseCase.findMessages(any())).thenReturn(new ChatbotMessageHistoryResult(List.of(), null, false));
+
+        mockMvc.perform(get("/api/chatbot/sessions/session-1/messages")
+                        .with(authentication(userAuthentication())))
+                .andExpect(status().isOk());
+
+        verify(messageQueryUseCase).findMessages(new FindChatbotMessagesQuery(7L, "session-1", null, 20));
+    }
+
     private Authentication userAuthentication() {
         AuthUser user = new AuthUser(7L, "member@example.com", "USER");
         return new UsernamePasswordAuthenticationToken(
                 user,
                 null,
                 List.of(new SimpleGrantedAuthority("USER"))
         );
     }
 }
