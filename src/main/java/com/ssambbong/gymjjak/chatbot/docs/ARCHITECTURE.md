# 🤖 챗봇 아키텍처

- 작성일: 2026-07-23
- 최종 수정일: 2026-07-23
- 상태: Spring Boot 챗봇 연동·영속화 **구현 예정 계약**

> 회원-트레이너 1:1 채팅과 별개의 회원 AI 챗봇 도메인입니다. 기존 `chat` 도메인의 테이블, topic, WebSocket 목적지는 변경하지 않습니다.

## 🎯 책임과 경계

```text
Frontend
  └─ STOMP /app/chatbot.send, /user/queue/chatbot
Spring Boot
  ├─ 인증·인가, 세션/메시지 영속화, 동시 스트림 제어, SSE 릴레이
  └─ FastAPI 호출
FastAPI
  └─ LangGraph/RAG/LLM 실행, 동일 HTTP 응답에서 SSE delta/done/error 전송
```

| 구성요소 | 책임 |
| --- | --- |
| `ChatbotWebSocketController` | 인증된 사용자의 STOMP 요청을 받고 비동기 스트림을 시작 |
| `ChatbotUseCase` / Service | 세션 소유권 검증, 사용자 메시지 선저장, 최근 문맥 조회, FastAPI SSE 소비 |
| `ChatbotSession` | 사용자별 대화 세션, 상태/마지막 활동 시각/진행 중 요청 식별자 보관 |
| `ChatbotMessage` | `USER`, `ASSISTANT` 메시지와 최종 응답 메타데이터 보관 |
| FastAPI | LLM 결과를 생성하며 Spring DB에는 직접 접근하지 않음 |

## 🗄️ 영속화 모델

신규 Spring 테이블/엔티티는 `chatbot_session`, `chatbot_message`입니다. 기존 `chat_room`, `chat_message` 및 `/topic/chat.room.{id}`는 이 기능과 공유하지 않습니다.

| 대상 | 핵심 필드 | 규칙 |
| --- | --- | --- |
| `chatbot_session` | `id`, `session_id(UUID)`, `user_id`, `title`, `status`, `last_activity_at`, `active_request_id`, `active_stream_expires_at`, 생성/수정 시각 | `session_id`는 외부에 노출하는 UUID이며 사용자 소유권 검증에 사용 |
| `chatbot_message` | `id`, `session_id(FK)`, `role`, `content`, `intent_hint`, `category`, `routine_json`, `sources_json`, `limited`, `created_at` | `role`은 `USER` 또는 `ASSISTANT`; 순서는 생성 시각과 ID로 결정 |

- Spring은 FastAPI 호출 전에 사용자 메시지를 저장하고 세션의 `last_activity_at`을 갱신합니다.
- FastAPI의 `done`을 받은 경우에만 완성된 assistant 메시지와 메타데이터를 저장합니다.
- FastAPI `error`로 끝난 경우에는 assistant 메시지를 저장하지 않습니다. WebSocket 연결 종료는 v1에서 FastAPI 호출을 취소하지 않으므로, 이후 `done`이 오면 assistant 메시지를 저장합니다.
- FastAPI 요청 본문에는 현재 사용자 메시지 **이전**의 최근 12개 메시지만 전달합니다. 사용자 메시지는 먼저 저장하되, 문맥 조회는 해당 새 행을 제외한 뒤 `message` 필드로 별도 전달해 중복 프롬프트를 막습니다.
- `sources_json`·`routine_json`·`limited`·`category`는 FastAPI `done` payload를 이력 조회에서 재현하기 위한 assistant 메타데이터입니다. USER 메시지에는 `intent_hint`만 선택적으로 저장합니다.

## 🔐 Spring → FastAPI 요청 계약

현재 FastAPI의 `ChatRequest`에는 이력 필드가 없고 `InMemoryConversationProvider`가 이력을 읽습니다. Spring 영속화 전환 때는 아래 snake_case 계약으로 **함께 변경**하며, FastAPI는 전달받은 `memory`를 우선 사용하고 자체 메모리 저장을 중단합니다.

```json
{
  "session_id": "UUID",
  "message": "현재 사용자 질문",
  "intent_hint": "선택 의도 힌트",
  "actor": { "user_id": 10, "role": "USER" },
  "memory": {
    "summary": null,
    "recent_messages": [
      { "role": "user", "content": "이전 질문" },
      { "role": "assistant", "content": "이전 답변" }
    ],
    "contexts": []
  }
}
```

- `actor`는 STOMP `Principal`에서 Spring이 생성하며 클라이언트 payload의 사용자 ID·역할은 받거나 신뢰하지 않습니다.
- 모든 요청에는 `X-Internal-Api-Key`와 Spring이 각 STOMP SEND 처리마다 새로 만든 `X-Request-ID`를 넣습니다. 기존 `X-Trace-Id`는 별도 로그 추적 키이며 재사용하지 않습니다.
- `memory.recent_messages`는 시간순이고 최대 12개입니다. 요약·컨텍스트 영속화는 테이블 모델이 확장되기 전까지 각각 `null`, 빈 배열로 보냅니다.

## ⚙️ 실행·추적 규칙

- 스트림 처리는 WebSocket inbound 스레드를 점유하지 않는 `chatbotStreamingTaskExecutor`에서 실행합니다. 이 전용 executor는 core 5/max 10/queue 50, MDC `requestId` 전파를 사용하며 작업 거절 시 즉시 `CHATBOT_STREAM_IN_PROGRESS`가 아닌 별도 `CHATBOT_STREAM_CAPACITY_EXCEEDED` 오류를 보냅니다.
- Spring은 STOMP native header가 아닌 서버 생성 UUID를 `requestId`로 사용하고, 이를 `started`/후속 이벤트와 FastAPI `X-Request-ID` 헤더에 동일하게 넣습니다.
- 같은 `sessionId`의 스트림은 `active_request_id IS NULL` 조건의 원자 UPDATE로 획득합니다. 만료 시각은 120초로 기록하고, 종료 시 `session_id + active_request_id`가 일치할 때만 해제해 다중 Spring 인스턴스에서도 다른 요청의 잠금을 지우지 않습니다. 만료 잠금은 정리 작업에서 회수합니다.
- WebSocket 연결이 끊겨도 v1에서는 FastAPI 호출을 취소하지 않습니다. 서버는 `done`까지 저장을 완료하고, 재연결한 클라이언트는 이력 API로 결과를 조회합니다.

## 🧹 보관 및 정리

- 세션 목록과 메시지 이력은 사용자 본인만 조회할 수 있습니다.
- 마지막 활동 후 6개월이 지난 비활성 세션과 종속 메시지는 배치 정리 대상입니다.
- 정리 대상 선정과 삭제는 트랜잭션으로 수행하며, 실행 결과는 운영 로그/메트릭으로 남깁니다.

## 📝 변경 이력

| 날짜 | 변경 내용 |
| --- | --- |
| 2026-07-23 | Spring 챗봇 세션·메시지 영속화, STOMP-SSE 릴레이, 동시성·보관 정책 설계 작성 |
