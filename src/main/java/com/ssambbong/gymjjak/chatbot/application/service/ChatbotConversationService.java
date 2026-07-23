package com.ssambbong.gymjjak.chatbot.application.service;

import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotSubscriptionAccessPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationStart;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotContextJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotMessageJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotContextRepository;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotMessageRepository;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatbotConversationService {

    private static final int STREAM_LOCK_SECONDS = 120;

    private final SpringDataChatbotSessionRepository sessionRepository;
    private final SpringDataChatbotMessageRepository messageRepository;
    private final SpringDataChatbotContextRepository contextRepository;
    private final ChatbotSubscriptionAccessPort subscriptionAccessPort;

    /**
     * STOMP 메시지를 FastAPI 요청으로 바꾸기 전의 영속화와 동시성 제어를 담당합니다.
     * 현재 질문은 저장하되, FastAPI에는 저장 전의 최근 대화만 메모리로 전달합니다.
     */
    @Transactional
    public ChatbotConversationStart prepare(SendChatbotMessageCommand command) {
        if (!subscriptionAccessPort.hasActiveAccess(command.userId())) {
            throw new ChatbotSessionException(ChatbotErrorCode.SUBSCRIPTION_REQUIRED);
        }
        LocalDateTime now = LocalDateTime.now();
        ChatbotSessionJpaEntity session = resolveSession(command.sessionId(), command.userId(), now);
        List<ChatbotMessageJpaEntity> recentMessages = messageRepository
                .findTop12BySessionIdOrderByCreatedAtDesc(session.getSessionId());
        String requestId = UUID.randomUUID().toString();

        int updated = sessionRepository.acquireStreamLock(
                session.getSessionId(), command.userId(), requestId, now.plusSeconds(STREAM_LOCK_SECONDS), now
        );
        if (updated == 0) {
            throw new ChatbotSessionException(ChatbotErrorCode.STREAM_IN_PROGRESS);
        }

        try {
            messageRepository.save(ChatbotMessageJpaEntity.user(
                    session.getSessionId(), command.content(), command.intentHint()
            ));
            session.touch(now);
            sessionRepository.save(session);
            List<ChatbotContextJpaEntity> contexts = contextRepository.findActiveBySessionIdAndUserId(
                    session.getSessionId(), command.userId(), now
            );
            return new ChatbotConversationStart(
                    session.getSessionId(),
                    requestId,
                    new ChatbotAiRequest(
                            session.getSessionId(), command.content(), command.intentHint(),
                            new ChatbotAiRequest.Actor(command.userId(), command.userRole()),
                            new ChatbotAiRequest.Memory(
                                    session.getSummary(),
                                    toChronologicalMessages(recentMessages),
                                    contexts.stream()
                                            .map(context -> new ChatbotAiRequest.Context(
                                                    context.getKind().name(), context.getValue()
                                            ))
                                            .toList()
                            ),
                            requestId
                    )
            );
        } catch (RuntimeException exception) {
            sessionRepository.releaseStreamLock(session.getSessionId(), requestId);
            throw exception;
        }
    }

    /** FastAPI의 done 이벤트만 assistant 메시지와 UI 복원용 메타데이터로 저장합니다. */
    @Transactional
    public void persistDone(ChatbotConversationStart start, ChatbotAiEvent.Done done) {
        if (!start.sessionId().equals(done.sessionId())) {
            throw new ChatbotSessionException(ChatbotErrorCode.FASTAPI_RESPONSE_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();
        messageRepository.save(ChatbotMessageJpaEntity.assistant(
                start.sessionId(), done.answer(), done.category(), done.routineJson(), done.sourcesJson(), done.limited()
        ));
        sessionRepository.findBySessionId(start.sessionId()).ifPresent(session -> session.touch(now));
    }

    /** 스트림 종료 여부와 무관하게 현재 요청이 잡은 잠금을 해제합니다. */
    @Transactional
    public void releaseStreamLock(ChatbotConversationStart start) {
        sessionRepository.releaseStreamLock(start.sessionId(), start.requestId());
    }

    /** 세션이 없으면 생성하고, 있으면 현재 사용자 소유인지 검증합니다. */
    private ChatbotSessionJpaEntity resolveSession(String sessionId, Long userId, LocalDateTime now) {
        if (sessionId == null || sessionId.isBlank()) {
            return sessionRepository.save(ChatbotSessionJpaEntity.create(userId, now));
        }
        ChatbotSessionJpaEntity session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ChatbotSessionException(ChatbotErrorCode.SESSION_NOT_FOUND));
        if (!session.isOwnedBy(userId)) {
            throw new ChatbotSessionException(ChatbotErrorCode.SESSION_ACCESS_DENIED);
        }
        return session;
    }

    /** DB의 최신순 메시지를 LLM 대화 순서인 오래된 순으로 되돌립니다. */
    private List<ChatbotAiRequest.Message> toChronologicalMessages(List<ChatbotMessageJpaEntity> messages) {
        List<ChatbotMessageJpaEntity> chronological = new ArrayList<>(messages);
        Collections.reverse(chronological);
        return chronological.stream()
                .map(message -> new ChatbotAiRequest.Message(
                        message.getRole().name().toLowerCase(), message.getContent()
                ))
                .toList();
    }
}
