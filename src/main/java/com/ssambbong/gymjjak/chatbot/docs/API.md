# 📚 챗봇 REST API

- 작성일: 2026-07-23
- 최종 수정일: 2026-07-23
- 기본 경로: `/api/chatbot/sessions`

> 메시지 생성은 REST가 아닌 [WEBSOCKET_API.md](WEBSOCKET_API.md)의 STOMP API를 사용합니다. 아래 API는 저장된 챗봇 세션과 이력 조회 전용입니다.
>
> `GET /api/chatbot/sessions`는 구현 완료되었습니다. 메시지 이력 조회 API는 구현 예정입니다.

## 📋 세션 목록 조회

`GET /api/chatbot/sessions?cursor={cursor}&size={size}`

로그인한 사용자의 세션만 `lastActivityAt DESC, sessionId DESC` 순으로 조회합니다.

| Query parameter | 필수 여부 | 설명 |
| --- | --- | --- |
| `cursor` | 선택 | 다음 페이지 조회용 커서입니다. `(lastActivityAt, sessionId)`를 인코딩한 불투명 값입니다. |
| `size` | 선택 | 조회 개수입니다. 기본값은 `20`이고, 허용 범위는 `1~50`입니다. |

- 서버는 `size + 1`개를 조회하여 다음 페이지 존재 여부를 판단합니다.
- `hasNext`가 `true`이면 `nextCursor`를 다음 요청의 `cursor`로 전달합니다. 마지막 페이지면 `hasNext`는 `false`, `nextCursor`는 `null`입니다.
- `sessionId`는 외부에 노출하는 UUID이며, DB 내부 PK인 `chatbot_session_id`는 응답하지 않습니다.
- `title`은 세션의 첫 번째 `USER` 메시지이고, `lastMessage`는 해당 세션의 최신 메시지입니다.

```json
{
  "status": 200,
  "code": "CHATBOT_SESSION_LIST_SUCCESS",
  "message": "챗봇 세션 목록 조회에 성공했습니다.",
  "data": {
    "sessions": [
      {
        "sessionId": "019f0000-0000-7000-8000-000000000001",
        "title": "하체 루틴 상담",
        "lastMessage": "이번 주는 하체를 두 번 나누어 진행해 보세요.",
        "lastActivityAt": "2026-07-23T10:00:00"
      }
    ],
    "nextCursor": null,
    "hasNext": false
  }
}
```

## 💬 메시지 이력 조회

> **구현 예정**

`GET /api/chatbot/sessions/{sessionId}/messages?cursor={cursor}&size={size}`

세션 소유자만 조회할 수 있습니다. 결과는 화면 재현을 위해 오래된 메시지부터 반환하며, `cursor`는 `(createdAt, messageId)`를 인코딩한 불투명 값입니다. 서버는 페이지 경계의 안정성을 위해 같은 정렬 키를 함께 사용합니다.

```json
{
  "status": 200,
  "code": "CHATBOT_MESSAGE_LIST_SUCCESS",
  "message": "챗봇 메시지 이력 조회에 성공했습니다.",
  "data": {
    "sessionId": "019f0000-0000-7000-8000-000000000001",
    "messages": [
      {
        "messageId": 101,
        "role": "USER",
        "content": "이번 주 하체 운동 루틴을 추천해줘.",
        "intentHint": "ROUTINE_RECOMMENDATION",
        "category": null,
        "routine": null,
        "sources": [],
        "createdAt": "2026-07-23T10:00:00"
      },
      {
        "messageId": 102,
        "role": "ASSISTANT",
        "content": "이번 주는 하체를 두 번 나누어 진행해 보세요.",
        "intentHint": null,
        "category": "ROUTINE",
        "routine": null,
        "sources": [],
        "limited": false,
        "createdAt": "2026-07-23T10:00:04"
      }
    ],
    "nextCursor": null,
    "hasNext": false
  }
}
```

## ⚠️ 공통 오류

| HTTP 상태 | code | 발생 조건 |
| --- | --- | --- |
| 400 | `COMMON_400` | 잘못된 cursor 또는 size 범위 위반 |
| 401 | `COMMON_401` | 인증되지 않은 요청 |
| 403 | `COMMON_403` | 다른 사용자의 세션 접근 |
| 404 | `CHATBOT_SESSION_NOT_FOUND` | 존재하지 않거나 정리된 세션 |

## 🧹 보관 정책

- 마지막 활동 후 6개월이 지난 비활성 세션은 정리 배치에서 종속 메시지와 함께 삭제합니다.
- 정리 완료 세션은 목록과 이력 조회에서 `CHATBOT_SESSION_NOT_FOUND`로 처리합니다.
- 메시지 생성 중에는 STOMP의 `started/delta/done/error` 이벤트를 사용하며, `error`로 끝난 assistant 응답은 이력에 남지 않습니다.

## 📝 변경 이력

| 날짜 | 변경 내용 |
| --- | --- |
| 2026-07-23 | 챗봇 세션 목록 조회 API 구현 계약 반영, 메시지 이력 조회는 구현 예정으로 유지 |
