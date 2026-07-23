# 챗봇 REST API

- 작성일: 2026-07-23
- 기준 경로: `/api/chatbot/sessions`

> 챗봇 메시지 생성과 스트리밍은 REST가 아니라 [WEBSOCKET_API.md](WEBSOCKET_API.md)의 STOMP API를 사용한다.

---

## 1. 챗봇 세션 목록 조회

로그인 사용자의 이전 챗봇 세션을 최근 활동 순으로 조회한다.

`GET /api/chatbot/sessions?cursor={cursor}&size={size}`

### Request Header

| name | required | description |
| --- | --- | --- |
| `Authorization` | Y | `Bearer {accessToken}` |

### Query Parameter

| name | required | description |
| --- | --- | --- |
| `cursor` | N | 이전 응답의 `nextCursor`. 없거나 공백이면 첫 페이지를 조회한다. |
| `size` | N | 조회 개수. 기본값 `20`, 허용 범위 `1~50` |

### Response

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `CHATBOT_SESSION_LIST_SUCCESS` | 챗봇 세션 목록 조회에 성공했습니다. |

```json
{
  "status": 200,
  "code": "CHATBOT_SESSION_LIST_SUCCESS",
  "message": "챗봇 세션 목록 조회에 성공했습니다.",
  "data": {
    "sessions": [
      {
        "sessionId": "019f0000-0000-7000-8000-000000000001",
        "title": "하체 운동 루틴 상담",
        "lastMessage": "이번 주는 하체를 주 2회로 나누어 진행해 보세요.",
        "lastActivityAt": "2026-07-23T10:00:00"
      }
    ],
    "nextCursor": "eyJsYXN0QWN0aXZpdHlBdCI6IjIwMjYtMDctMjNUMTA6MDA6MDAiLCJzZXNzaW9uSWQiOiIwMTlmMDAwMC0wMDAwLTcwMDAtODAwMC0wMDAwMDAwMDAwMDEifQ",
    "hasNext": true
  }
}
```

| field | description |
| --- | --- |
| `data.sessions[].sessionId` | 외부에 노출하는 세션 UUID. DB PK `chatbot_session_id`는 노출하지 않는다. |
| `data.sessions[].title` | 세션의 첫 번째 `USER` 메시지 내용 |
| `data.sessions[].lastMessage` | 세션의 최신 메시지 내용 (`USER`, `ASSISTANT` 포함) |
| `data.sessions[].lastActivityAt` | 세션 마지막 활동 시각. ISO-8601 `LocalDateTime` |
| `data.nextCursor` | 다음 페이지 cursor. 마지막 페이지면 `null` |
| `data.hasNext` | 다음 페이지 존재 여부 |

정렬은 `lastActivityAt DESC, sessionId DESC`이며, 서버는 `size + 1`개를 읽어 다음 페이지 존재 여부를 판단한다.

---

## 2. 챗봇 메시지 이력 조회

로그인 사용자가 소유한 세션의 메시지 이력을 조회한다. DB에서는 최신 메시지부터 조회하지만, 프론트가 바로 렌더링하도록 응답 배열은 오래된 메시지부터 최신 메시지 순으로 반환한다. 이전 대화는 화면을 위로 스크롤할 때 `nextCursor`로 추가 조회한다.

`GET /api/chatbot/sessions/{sessionId}/messages?cursor={cursor}&size={size}`

### Request Header

| name | required | description |
| --- | --- | --- |
| `Authorization` | Y | `Bearer {accessToken}` |

### Path / Query Parameter

| name | location | required | description |
| --- | --- | --- | --- |
| `sessionId` | path | Y | 조회할 챗봇 세션 UUID |
| `cursor` | query | N | 이전 응답의 `nextCursor`. 없거나 공백이면 최신 메시지부터 첫 페이지를 조회한다. |
| `size` | query | N | 조회 개수. 기본값 `20`, 허용 범위 `1~50` |

### Response

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `CHATBOT_MESSAGE_HISTORY_SUCCESS` | 챗봇 메시지 이력 조회에 성공했습니다. |

```json
{
  "status": 200,
  "code": "CHATBOT_MESSAGE_HISTORY_SUCCESS",
  "message": "챗봇 메시지 이력 조회에 성공했습니다.",
  "data": {
    "messages": [
      {
        "messageId": 101,
        "role": "USER",
        "content": "하체 운동 루틴을 추천해줘",
        "intentHint": null,
        "category": null,
        "routine": null,
        "sources": [],
        "limited": null,
        "createdAt": "2026-07-23T09:58:00"
      },
      {
        "messageId": 102,
        "role": "ASSISTANT",
        "content": "주 2회 하체 루틴을 제안할게요.",
        "intentHint": "ROUTINE",
        "category": "ROUTINE",
        "routine": { "name": "하체 루틴" },
        "sources": [{ "title": "운동 가이드" }],
        "limited": false,
        "createdAt": "2026-07-23T09:59:00"
      }
    ],
    "nextCursor": "eyJjcmVhdGVkQXQiOiIyMDI2LTA3LTIzVDA5OjU4OjAwIiwibWVzc2FnZUlkIjoxMDF9",
    "hasNext": true
  }
}
```

| field | description |
| --- | --- |
| `data.messages[].messageId` | 메시지 식별자 |
| `data.messages[].role` | 발신자 역할: `USER`, `ASSISTANT` |
| `data.messages[].content` | 메시지 본문 |
| `data.messages[].intentHint` | assistant 응답의 의도 힌트. 없으면 `null` |
| `data.messages[].category` | assistant 응답 카테고리. 없으면 `null` |
| `data.messages[].routine` | assistant 루틴 JSON. 없으면 `null` |
| `data.messages[].sources` | assistant 출처 JSON 배열. 출처가 없으면 `[]` |
| `data.messages[].limited` | 응답 제한 여부. 없으면 `null` |
| `data.messages[].createdAt` | 메시지 생성 시각. ISO-8601 `LocalDateTime` |
| `data.nextCursor` | 더 과거 메시지를 조회하는 cursor. 더 없으면 `null` |
| `data.hasNext` | 더 과거 메시지 존재 여부 |

페이지네이션 기준은 `createdAt DESC, messageId DESC`이다. 서버는 최신순으로 `size + 1`개를 조회한 후 응답 대상만 오래된 순으로 뒤집는다. `hasNext=true`일 때 `nextCursor`는 이번 응답의 가장 오래된 메시지를 기준으로 생성한다. 메시지가 없는 정상 세션은 `messages: []`, `hasNext: false`, `nextCursor: null`을 반환한다.

### Error Response

| HTTP 상태 | code | 발생 조건 |
| --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | `size`가 `1~50` 범위를 벗어남, 숫자 변환 실패, 잘못된 cursor(Base64/JSON/필수 값 누락) |
| `401 Unauthorized` | `AUTH_401_xxx` | Access Token 누락·만료·위조 또는 claim 검증 실패 |
| `403 Forbidden` | `CHATBOT_SESSION_ACCESS_DENIED` | 다른 사용자의 세션 이력 조회 시도 |
| `404 Not Found` | `CHATBOT_SESSION_NOT_FOUND` | 존재하지 않는 세션 조회 |
| `500 Internal Server Error` | `COMMON_500` | 예상하지 못한 서버 오류 |

---

## 문서 변경 이력

| 날짜 | 내용 |
| --- | --- |
| 2026-07-23 | 세션 목록 조회 API와 메시지 이력 조회 API 계약을 구현 기준으로 최신화 |
