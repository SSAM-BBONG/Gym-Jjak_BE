package com.ssambbong.gymjjak.chatbot.presentation.api;

import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotMessageQueryUseCase;
import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotSessionQueryUseCase;
import com.ssambbong.gymjjak.chatbot.presentation.api.request.FindChatbotMessagesRequest;
import com.ssambbong.gymjjak.chatbot.presentation.api.request.FindChatbotSessionsRequest;
import com.ssambbong.gymjjak.chatbot.presentation.api.response.ChatbotResponseCode;
import com.ssambbong.gymjjak.chatbot.presentation.api.response.ChatbotMessageHistoryResponse;
import com.ssambbong.gymjjak.chatbot.presentation.api.response.ChatbotSessionListResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot/sessions")
@RequiredArgsConstructor
public class ChatbotSessionController {

    private final ChatbotSessionQueryUseCase queryUseCase;
    private final ChatbotMessageQueryUseCase messageQueryUseCase;

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

    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<GlobalApiResponse<ChatbotMessageHistoryResponse>> findMessages(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable String sessionId,
            @ModelAttribute @Valid FindChatbotMessagesRequest request
    ) {
        ChatbotMessageHistoryResult result = messageQueryUseCase.findMessages(
                new FindChatbotMessagesQuery(authUser.userId(), sessionId, request.cursor(), request.resolveSize())
        );
        return ResponseEntity.ok(GlobalApiResponse.ok(
                ChatbotResponseCode.CHATBOT_MESSAGE_HISTORY_SUCCESS,
                ChatbotMessageHistoryResponse.from(result)
        ));
    }
}
