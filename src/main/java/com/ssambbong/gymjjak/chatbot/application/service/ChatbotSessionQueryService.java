package com.ssambbong.gymjjak.chatbot.application.service;

import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListItem;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotSessionQueryUseCase;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionListRow;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotSessionQueryService implements ChatbotSessionQueryUseCase {

    private final SpringDataChatbotSessionRepository sessionRepository;
    private final ChatbotSessionCursorCodec cursorCodec;

    @Override
    public ChatbotSessionListResult findSessions(FindChatbotSessionsQuery query) {
        ChatbotSessionCursorCodec.CursorPayload cursor = decodeCursor(query.cursor());
        List<ChatbotSessionListRow> rows = sessionRepository.findSessionList(
                query.userId(),
                cursor == null ? null : cursor.lastActivityAt(),
                cursor == null ? null : cursor.sessionId(),
                query.size() + 1
        );
        boolean hasNext = rows.size() > query.size();
        List<ChatbotSessionListItem> sessions = rows.stream()
                .limit(query.size())
                .map(this::toListItem)
                .toList();
        ChatbotSessionListItem lastSession = hasNext ? sessions.get(sessions.size() - 1) : null;
        String nextCursor = hasNext
                ? cursorCodec.encode(lastSession.lastActivityAt(), lastSession.sessionId())
                : null;

        return new ChatbotSessionListResult(sessions, nextCursor, hasNext);
    }

    private ChatbotSessionCursorCodec.CursorPayload decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        return cursorCodec.decode(cursor);
    }

    private ChatbotSessionListItem toListItem(ChatbotSessionListRow row) {
        return new ChatbotSessionListItem(
                row.getSessionId(), row.getTitle(), row.getLastMessage(), row.getLastActivityAt()
        );
    }
}
