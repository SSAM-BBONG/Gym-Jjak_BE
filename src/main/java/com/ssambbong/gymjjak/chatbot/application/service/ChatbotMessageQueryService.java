package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryItem;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotMessageQueryUseCase;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotMessageRepository;
import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotMessageQueryService implements ChatbotMessageQueryUseCase {

    private final ChatbotSessionRepository sessionRepository;
    private final ChatbotMessageRepository messageRepository;
    private final ChatbotMessageCursorCodec cursorCodec;
    private final ObjectMapper objectMapper;

    @Override
    public ChatbotMessageHistoryResult findMessages(FindChatbotMessagesQuery query) {
        ChatbotSession session = sessionRepository.findBySessionId(query.sessionId())
                .orElseThrow(() -> new ChatbotSessionException(ChatbotErrorCode.SESSION_NOT_FOUND));
        if (!session.isOwnedBy(query.userId())) {
            throw new ChatbotSessionException(ChatbotErrorCode.SESSION_ACCESS_DENIED);
        }

        ChatbotMessageCursorCodec.CursorPayload cursor = decodeCursor(query.cursor());
        List<ChatbotMessage> rows = messageRepository.findHistory(
                query.sessionId(),
                cursor == null ? null : cursor.createdAt(),
                cursor == null ? null : cursor.messageId(),
                query.size() + 1
        );
        boolean hasNext = rows.size() > query.size();
        List<ChatbotMessage> retainedRows = new ArrayList<>(rows.subList(0, Math.min(rows.size(), query.size())));
        ChatbotMessage oldestRetainedMessage = retainedRows.get(retainedRows.size() - 1);
        String nextCursor = hasNext
                ? cursorCodec.encode(oldestRetainedMessage.createdAt(), oldestRetainedMessage.messageId())
                : null;

        Collections.reverse(retainedRows);
        List<ChatbotMessageHistoryItem> messages = retainedRows.stream().map(this::toHistoryItem).toList();
        return new ChatbotMessageHistoryResult(messages, nextCursor, hasNext);
    }

    private ChatbotMessageCursorCodec.CursorPayload decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        return cursorCodec.decode(cursor);
    }

    private ChatbotMessageHistoryItem toHistoryItem(ChatbotMessage message) {
        return new ChatbotMessageHistoryItem(
                message.messageId(), message.role(), message.content(), message.intentHint(), message.category(),
                parseNullableJson(message.routineJson()), parseSources(message.sourcesJson()),
                message.limited(), message.createdAt()
        );
    }

    private JsonNode parseNullableJson(String value) {
        return value == null ? null : readJson(value);
    }

    private JsonNode parseSources(String value) {
        return value == null ? objectMapper.createArrayNode() : readJson(value);
    }

    private JsonNode readJson(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("저장된 챗봇 메시지 메타데이터를 읽을 수 없습니다.", exception);
        }
    }
}
