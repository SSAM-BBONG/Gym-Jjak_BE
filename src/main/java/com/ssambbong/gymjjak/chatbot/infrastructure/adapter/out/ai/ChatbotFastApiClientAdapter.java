package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiClientPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotAiException;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
public class ChatbotFastApiClientAdapter implements ChatbotAiClientPort {

    private static final String CHATBOT_MESSAGE_PATH = "/api/v1/chatbot/messages";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ChatbotFastApiClientAdapter(
            // global 도메인의 공통 RestClient 사용
            @Qualifier("aiServiceRestClient") RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    // eventConsumer == handleFastApiEvent
    @Override
    public void stream(ChatbotAiRequest request, Consumer<ChatbotAiEvent> eventConsumer) {
        try {
            restClient.post()
                    .uri(CHATBOT_MESSAGE_PATH)  // fastAPI 서버 챗봇 엔드포인트 주소
                    // X-Request-ID, 모니터링용 requestId 전송
                    .header(REQUEST_ID_HEADER, request.requestId())
                    .body(FastApiRequest.from(request)) // 앞에서 만든 요청 본문
                    // 응답 받고 SSE 스트림 읽기 시작
                    .exchange((httpRequest, response) -> {
                        if (response.getStatusCode().isError()) {
                            log.warn("event=chatbot_fastapi_error status={} requestId={}",
                                    response.getStatusCode(), request.requestId());
                            throw new ChatbotAiException(ChatbotErrorCode.FASTAPI_REQUEST_FAILED);
                        }
                        // SSE 스트림 읽기
                        readSse(response.getBody(), eventConsumer);
                        return null;
                    });
        } catch (ChatbotAiException exception) {
            throw exception;
        } catch (ResourceAccessException exception) {
            throw new ChatbotAiException(resolveErrorCode(exception), exception);
        } catch (RestClientException exception) {
            throw new ChatbotAiException(ChatbotErrorCode.FASTAPI_REQUEST_FAILED, exception);
        }
    }

    // SSE 포멧, 스트리밍 읽기
    private void readSse(java.io.InputStream body, Consumer<ChatbotAiEvent> eventConsumer) {
        // BufferedReader로 줄 단위로 읽기
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {
            String eventName = null;
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {    // 줄 계속 읽기
                // 빈 줄일 경우
                if (line.isEmpty()) {
                    dispatch(eventName, data, eventConsumer); // delta, done 선택 이벤트 처리
                    eventName = null;
                    data.setLength(0);
                    continue;
                }
                if (line.startsWith("event:")) {
                    // event : 줄 파싱
                    eventName = line.substring("event:".length()).trim();
                    // "event: delta" → eventName = "delta"
                } else if (line.startsWith("data:")) {
                    if (!data.isEmpty()) {
                        data.append('\n');
                    }
                    data.append(line.substring("data:".length()).trim());
                    // "data: {"text": "좋습니다. "}" → data = {"text": "좋습니다. "}
                }
            }
            dispatch(eventName, data, eventConsumer);   // 마지막 이벤트 처리
        } catch (IOException exception) {
            throw new ChatbotAiException(ChatbotErrorCode.FASTAPI_RESPONSE_INVALID, exception);
        }
    }

    // 이벤트 변환 및 롤백 메서드
    private void dispatch(String eventName, StringBuilder data, Consumer<ChatbotAiEvent> eventConsumer) {
        // 유효 x 이벤트는 return
        if (eventName == null || data.isEmpty()) {
            return;
        }
        try {
            JsonNode payload = objectMapper.readTree(data.toString()); // json 파싱
            switch (eventName) {
                // delta 이벤트 Controller로 전달
                case "delta" -> eventConsumer.accept(
                        new ChatbotAiEvent.Delta(requiredText(payload, "text"))
                );
                // done 이벤트 Controller로 전달
                case "done" -> eventConsumer.accept(new ChatbotAiEvent.Done(
                        requiredText(payload, "session_id"),
                        requiredText(payload, "answer"),
                        nullableText(payload, "category"),
                        jsonValue(payload, "routine"),
                        jsonValue(payload, "sources"),
                        payload.path("limited").asBoolean(false),
                        jsonValueOrEmptyArray(payload, "quick_replies")
                ));
                // 예외도 controller에 전달
                case "error" -> eventConsumer.accept(new ChatbotAiEvent.Error(
                        requiredText(payload, "code"),
                        requiredText(payload, "message"),
                        payload.path("retryable").asBoolean(false)
                ));
                default -> log.debug("event=chatbot_fastapi_unknown_sse_event event={}", eventName);
            }
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            throw new ChatbotAiException(ChatbotErrorCode.FASTAPI_RESPONSE_INVALID, exception);
        }
    }

    private String requiredText(JsonNode payload, String field) {
        String value = nullableText(payload, field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("missing required SSE field: " + field);
        }
        return value;
    }

    private String nullableText(JsonNode payload, String field) {
        JsonNode value = payload.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String jsonValue(JsonNode payload, String field) throws JsonProcessingException {
        JsonNode value = payload.get(field);
        return value == null || value.isNull() ? null : objectMapper.writeValueAsString(value);
    }

    private String jsonValueOrEmptyArray(JsonNode payload, String field) throws JsonProcessingException {
        String value = jsonValue(payload, field);
        return value == null ? "[]" : value;
    }

    private ChatbotErrorCode resolveErrorCode(ResourceAccessException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause.getClass().getSimpleName().toLowerCase().contains("timeout")) {
                return ChatbotErrorCode.FASTAPI_TIMEOUT;
            }
            cause = cause.getCause();
        }
        return ChatbotErrorCode.FASTAPI_REQUEST_FAILED;
    }

    private record FastApiRequest(
            @JsonProperty("session_id") String sessionId,
            String message,
            @JsonProperty("intent_hint") String intentHint,
            Actor actor,
            Memory memory
    ) {
        static FastApiRequest from(ChatbotAiRequest request) {
            return new FastApiRequest(
                    request.sessionId(), request.message(), request.intentHint(),
                    Actor.from(request.actor()), Memory.from(request.memory())
            );
        }
    }

    private record Actor(@JsonProperty("user_id") Long userId, String role) {
        static Actor from(ChatbotAiRequest.Actor actor) {
            return new Actor(actor.userId(), actor.role());
        }
    }

    private record Memory(
            String summary,
            @JsonProperty("recent_messages") List<Message> recentMessages,
            List<Context> contexts
    ) {
        static Memory from(ChatbotAiRequest.Memory memory) {
            return new Memory(
                    memory.summary(),
                    memory.recentMessages().stream().map(Message::from).toList(),
                    memory.contexts().stream().map(Context::from).toList()
            );
        }
    }

    private record Message(String role, String content) {
        static Message from(ChatbotAiRequest.Message message) {
            return new Message(message.role(), message.content());
        }
    }

    private record Context(String kind, String value) {
        static Context from(ChatbotAiRequest.Context context) {
            return new Context(context.kind(), context.value());
        }
    }
}
