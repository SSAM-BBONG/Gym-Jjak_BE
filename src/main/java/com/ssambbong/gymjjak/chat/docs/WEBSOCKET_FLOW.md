# 💬 채팅 WebSocket 내부 흐름

> 이 문서는 STOMP WebSocket 메시지 전송의 서버 내부 흐름을 설명합니다.
> 클라이언트 연결·구독·전송 스펙은 [WEBSOCKET_API.md](WEBSOCKET_API.md)를 참고합니다.

---

## 1. 책임 범위

| 구성요소 | 책임 |
| --- | --- |
| `JwtHandshakeInterceptor` | HTTP 핸드셰이크 시 `accessToken` 쿠키 → WebSocket 세션 attributes에 저장 |
| `JwtChannelInterceptor` | STOMP CONNECT 프레임 수신 시 세션 attributes에서 토큰 복원 → `Authentication` 설정 |
| `ChatWebSocketController` | `@MessageMapping`으로 메시지 수신, 저장, 브로드캐스트 |
| `ChatMessageService` | 참여자 검증, 메시지 저장 |
| `SimpMessagingTemplate` | `/topic/chat.room.{id}`로 브로드캐스트 |
| `SimpUserRegistry` | 현재 topic 구독자 수 조회 (즉시 읽음 처리 판단용) |

---

## 2. 연결 및 인증 흐름

```text
클라이언트 (SockJS)
  → HTTP 핸드셰이크 요청 (/ws)
  → JwtHandshakeInterceptor
      → HttpServletRequest에서 accessToken 쿠키 추출
      → 유효하면 WebSocketSession.attributes["accessToken"] 에 저장
      → 없거나 유효하지 않으면 핸드셰이크 거부 (연결 실패)
  → WebSocket 연결 수립
  → STOMP CONNECT 프레임 전송
  → JwtChannelInterceptor (configureClientInboundChannel)
      → session.attributes["accessToken"] 조회
      → JWT 파싱 → AuthUser 생성 → UsernamePasswordAuthenticationToken 생성
      → StompHeaderAccessor.setUser(authentication) 설정
  → STOMP 연결 완료
```

### 핵심 포인트

- 쿠키 기반 인증이므로 STOMP CONNECT 헤더에 별도 토큰을 담을 필요가 없습니다.
- `JwtHandshakeInterceptor`가 HTTP 단계에서 쿠키를 읽고, `JwtChannelInterceptor`가 STOMP 단계에서 실제 `Authentication`을 설정하는 2단계 구조입니다.
- `@MessageMapping` 메서드에서 `Principal principal`로 인증 객체를 받고, `(AuthUser) ((Authentication) principal).getPrincipal()`로 사용자 정보를 추출합니다.

---

## 3. 메시지 전송 흐름

```text
클라이언트
  → STOMP SEND /app/chat.send
  → ChatWebSocketController.sendMessage(@Payload SendChatMessageRequest, Principal)

  [인증 추출]
  → (AuthUser) ((Authentication) principal).getPrincipal()
  → authUser.userId() 획득

  [메시지 저장]
  → SendChatMessageCommand (chatRoomId, senderId, content)
  → ChatMessageService.createMessage(command)
      → validateParticipant(chatRoomId, senderId)
          → ChatRoomRepository.findById()         ← 없으면 CHAT_001 (에러 큐로 전달)
          → status != ACTIVE                       ← CHAT_003
          → 참여자 검증 (USER or TRAINER)          ← CHAT_003
      → ChatMessage.create(chatRoomId, senderId, content)
      → ChatMessageRepository.save()
      → [커밋 이후] 메트릭 기록

  [즉시 읽음 처리 판단]
  → destination = "/topic/chat.room." + chatRoomId
  → SimpUserRegistry.findSubscriptions(s -> s.getDestination().equals(destination))
  → 구독자 수 == 2 → isRead = true → ChatMessageService.markAsRead(messageId)
  → 구독자 수 != 2 → isRead = false

  [브로드캐스트]
  → SimpMessagingTemplate.convertAndSend(destination, ChatMessageBroadcast)
  → 구독 중인 모든 클라이언트에 메시지 전달
```

### 즉시 읽음 처리 로직

| 조건 | 결과 |
| --- | --- |
| 해당 topic 구독자가 정확히 2명 (발신자 + 수신자 모두 접속 중) | `read = true`로 저장, broadcast에 `read = true` 포함 |
| 구독자가 1명 (상대방 미접속) | `read = false`로 저장, broadcast에 `read = false` 포함 |

> 구독자 수로 읽음 여부를 판단하는 단순 방식입니다. 다중 탭·다중 기기 접속 시 의도치 않게 읽음 처리될 수 있습니다.
> 추후 명시적 읽음 ACK 방식으로 개선이 예정되어 있습니다. → [채팅 리팩토링 계획 참고]

---

## 4. WebSocket 에러 처리 흐름

```text
@MessageExceptionHandler(ApplicationException.class)
  → ChatErrorResponse(timestamp, code, message) 생성
  → 메트릭 기록 (에러 코드 태그)
  → @SendToUser("/queue/errors")
  → 해당 사용자의 /user/queue/errors 로 에러 메시지 전달

@MessageExceptionHandler(Exception.class)
  → 예상치 못한 예외
  → code = "INTERNAL_ERROR"
  → 에러 로그 기록 (ERROR 레벨)
  → 메트릭 기록
  → @SendToUser("/queue/errors")
```

### 포인트

- `ApplicationException`은 비즈니스 예외(참여자 불일치, 채팅방 없음 등)이며, 에러 코드를 그대로 클라이언트에 전달합니다.
- 예상치 못한 예외는 내부 정보를 노출하지 않고 `INTERNAL_ERROR` 코드로만 응답합니다.
- 에러는 브로드캐스트가 아닌 발신자 개인 큐(`/user/queue/errors`)로만 전달됩니다.

---

## 5. 전체 메시지 생명주기

```text
[전송]
클라이언트 A → /app/chat.send
  → 저장 (is_read 초기값 false)
  → 즉시 읽음 판단
  → /topic/chat.room.{id} 브로드캐스트

[수신]
클라이언트 B (구독 중) → ChatMessageBroadcast 수신
클라이언트 A (발신자) → 동일 브로드캐스트 수신 (본인 메시지 echo)

[과거 메시지]
클라이언트 입장 시 → GET /api/chat/rooms/{chatRoomId}/messages 로 이전 메시지 조회
```

---

## 📝 문서 정보

- 작성일: `2026-07-21`
