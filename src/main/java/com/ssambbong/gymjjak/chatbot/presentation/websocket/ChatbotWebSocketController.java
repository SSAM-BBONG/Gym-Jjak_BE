package com.ssambbong.gymjjak.chatbot.presentation.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiClientPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationStart;
import com.ssambbong.gymjjak.chatbot.application.service.ChatbotConversationService;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.request.SendChatbotMessageRequest;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotDeltaEvent;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotDoneEvent;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotErrorEvent;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotStartedEvent;
import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatbotWebSocketController {

    private static final String USER_DESTINATION = "/queue/chatbot";

    private final ChatbotConversationService conversationService;
    private final ChatbotAiClientPort chatbotAiClientPort;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    @Qualifier("chatbotStreamingTaskExecutor")
    private final TaskExecutor chatbotStreamingTaskExecutor;

    /* Comment
    *   front로 부터 STOMP 메시지 받는 진입점
    *   해당 경로로 메시지 보내면 Spring의 WebSocket Broker가 이 메서드를 자동으로 호출
    * */
    @MessageMapping("/chatbot.send")
    public void sendMessage(
            @Payload @Valid SendChatbotMessageRequest request,
            Principal principal     // 웹소켓 핸드쉐이크 인증 증거, AuthUser로 캐스팅 사용
    ) {
        /* Comment
        *   1. principal를 Authentication 인터페이스로 캐스팅
        *   2. .getPrincipal() : Authentication에서 사용자 정보 추출
        *   3. AuthUser : 우리가 사용하는 AuthUser로 최종 캐스팅
        *   => 클라이언트의 userId를 신뢰하지 않고, AuthUser의 userId만 사용하기 위해서
        * */
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();

        // 사용자 입력 값 + authUser 를 통해 커맨드 생성
        // ChatbotConversationStart 에서 fastApiRequest 즉, fastAPI 요청 데이터
        ChatbotConversationStart start = conversationService.prepare(
                new SendChatbotMessageCommand(
                    request.sessionId(),
                    authUser.userId(),
                    authUser.role(),
                    request.content(),
                    request.intentHint(),
                    request.quickReply()
                )
        );

        // prepare 성공 시, 프론트한테 시작 이벤트 발행
        send(authUser, ChatbotStartedEvent.of(start.sessionId(), start.requestId()));

        try {
            // 비동기 스레드에서 스트리밍 시작
            chatbotStreamingTaskExecutor.execute(() -> stream(authUser, start));
        } catch (RuntimeException exception) {
            // prepare()로 인해 잠긴 락을 해제
            conversationService.releaseStreamLock(start);

            log.warn("event=chatbot_stream_rejected requestId={} exception={}", start.requestId(), exception.toString());

            // 프론트에 에러 알림
            send(authUser, ChatbotErrorEvent.of(
                    start.sessionId(),
                    start.requestId(),
                    "CHATBOT_STREAM_CAPACITY_EXCEEDED",
                    "챗봇 응답 처리 용량이 초과되었습니다.",
                    true    // 사용자가 다시 채팅 시도 가능
            ));
        }
    }

    private void stream(AuthUser authUser, ChatbotConversationStart start) {
        try {
            chatbotAiClientPort.stream(start.fastApiRequest(), event -> handleFastApiEvent(authUser, start, event));
        } catch (ApplicationException exception) {
            send(authUser, ChatbotErrorEvent.of(
                    start.sessionId(), start.requestId(), exception.getErrorCode().getCode(), exception.getMessage(), true
            ));
        } catch (RuntimeException exception) {
            log.error("event=chatbot_stream_failed requestId={}", start.requestId(), exception);
            send(authUser, ChatbotErrorEvent.of(
                    start.sessionId(), start.requestId(), "INTERNAL_ERROR", "챗봇 응답 생성 중 오류가 발생했습니다.", true
            ));
        } finally {
            conversationService.releaseStreamLock(start);
        }
    }

    private void handleFastApiEvent(AuthUser authUser, ChatbotConversationStart start, ChatbotAiEvent event) {
        // delta event인지 확인
        if (event instanceof ChatbotAiEvent.Delta delta) {
            // FastAPI 응답 내용 담은 send 호출,
            send(authUser, ChatbotDeltaEvent.of(start.sessionId(), start.requestId(), delta.text()));
            return;
        }
        // Done event 확인
        if (event instanceof ChatbotAiEvent.Done done) {
            // done 이벤트 저장 및 최종 문자 저장
            conversationService.persistDone(start, done);
            // Done 이벤트 프론트로 전송
            send(authUser, ChatbotDoneEvent.of(start.requestId(), done, objectMapper));
            return;
        }
        ChatbotAiEvent.Error error = (ChatbotAiEvent.Error) event;
        send(authUser, ChatbotErrorEvent.of(
                start.sessionId(), start.requestId(), error.code(), error.message(), error.retryable()
        ));
    }

    // 프론트한테 시작 이벤트 보내는 메서드
    private void send(AuthUser authUser, Object event) {
        // convertAndSendToUser : stomp 프로토콜로 특정 사용자 큐에 전송, 다른 사용자는 안보임
        messagingTemplate.convertAndSendToUser(
                authUser.getName(), // 특정 사용자 식별
                USER_DESTINATION,   // /queue/chatbot 경로
                event   // 이벤트 객체
        );
    }

    @MessageExceptionHandler(ApplicationException.class)
    @SendToUser(USER_DESTINATION)
    public ChatbotErrorEvent handleApplicationException(ApplicationException exception) {
        return ChatbotErrorEvent.of(null, null, exception.getErrorCode().getCode(), exception.getMessage(), false);
    }
}
