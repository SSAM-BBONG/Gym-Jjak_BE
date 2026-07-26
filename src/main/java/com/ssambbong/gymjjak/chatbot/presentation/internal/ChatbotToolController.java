package com.ssambbong.gymjjak.chatbot.presentation.internal;

import com.ssambbong.gymjjak.chatbot.application.result.ChatbotInbodySnapshot;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotWorkoutHistorySnapshot;
import com.ssambbong.gymjjak.chatbot.application.service.ChatbotToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * FastAPI Function Calling 전용 내부 API입니다.
 * 외부 클라이언트 API와 분리하기 위해 `/internal` 경로를 사용하며 사용자 ID는 받지 않습니다.
 */
@RestController
@RequestMapping("/internal/chatbot/tools")
@RequiredArgsConstructor
public class ChatbotToolController {

    private static final String SESSION_ID_HEADER = "X-Chatbot-Session-Id";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private final ChatbotToolService chatbotToolService;

    /** FastAPI가 최신 인바디가 필요한 답변을 만들 때 호출합니다. */
    @GetMapping("/inbody/latest")
    public ResponseEntity<ChatbotToolResponse<ChatbotInbodySnapshot>> loadLatestInbody(
            @RequestHeader(SESSION_ID_HEADER) String sessionId,
            @RequestHeader(REQUEST_ID_HEADER) String requestId
    ) {
        return ResponseEntity.ok(new ChatbotToolResponse<>(
                chatbotToolService.loadLatestInbody(sessionId, requestId)
        ));
    }

    /** FastAPI가 기간별 운동 일지가 필요한 답변을 만들 때 호출합니다. */
    @GetMapping("/workout-history")
    public ResponseEntity<ChatbotToolResponse<ChatbotWorkoutHistorySnapshot>> loadWorkoutHistory(
            @RequestHeader(SESSION_ID_HEADER) String sessionId,
            @RequestHeader(REQUEST_ID_HEADER) String requestId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(new ChatbotToolResponse<>(
                chatbotToolService.loadWorkoutHistory(sessionId, requestId, from, to)
        ));
    }
}
