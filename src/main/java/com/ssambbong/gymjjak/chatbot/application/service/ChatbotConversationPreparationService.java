package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.model.ChatbotQuickReply;
import com.ssambbong.gymjjak.chatbot.application.model.RoutinePreferenceContext;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotSubscriptionAccessPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationPreparation;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotContextKind;
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
public class ChatbotConversationPreparationService {
    private static final int STREAM_LOCK_SECONDS = 120;

    private final SpringDataChatbotSessionRepository sessionRepository;
    private final SpringDataChatbotMessageRepository messageRepository;
    private final SpringDataChatbotContextRepository contextRepository;
    private final ChatbotSubscriptionAccessPort subscriptionAccessPort;
    private final ObjectMapper objectMapper;

    // 무거운 스냅샷 조회 전에 스트림 락과 가벼운 채팅 상태만 저장하고 커밋한다.
    @Transactional
    public ChatbotConversationPreparation prepare(SendChatbotMessageCommand command) {
        // 접근 권한이 없으면 세션 생성과 락 획득을 수행하지 않는다.
        if (!subscriptionAccessPort.hasActiveAccess(command.userId())) {
            throw new ChatbotSessionException(ChatbotErrorCode.SUBSCRIPTION_REQUIRED);
        }

        // 세션 생성 또는 소유권 검증 후, FastAPI에 전달할 이전 대화를 조회한다.
        LocalDateTime now = LocalDateTime.now();
        ChatbotSessionJpaEntity session = resolveSession(command.sessionId(), command.userId(), now);
        List<ChatbotMessageJpaEntity> recentMessages = messageRepository
                .findTop12BySessionIdOrderByCreatedAtDesc(session.getSessionId());
        String requestId = UUID.randomUUID().toString();

        // 동일 세션의 스트림이 비어 있을 때만 원자적으로 실행 권한을 획득한다.
        int updated = sessionRepository.acquireStreamLock(
                session.getSessionId(), command.userId(), requestId,
                now.plusSeconds(STREAM_LOCK_SECONDS), now
        );
        if (updated == 0) {
            throw new ChatbotSessionException(ChatbotErrorCode.STREAM_IN_PROGRESS);
        }

        try {
            // 락 획득에 성공한 요청만 선택지 반영과 USER 메시지 저장을 진행한다.
            applyQuickReplyIfPresent(session, command, now);
            messageRepository.save(ChatbotMessageJpaEntity.user(
                    session.getSessionId(), command.content(), command.intentHint()
            ));
            List<ChatbotContextJpaEntity> contexts = contextRepository.findActiveBySessionIdAndUserId(
                    session.getSessionId(), command.userId(), now
            );
            return new ChatbotConversationPreparation(
                    session.getSessionId(), requestId, command.content(), command.intentHint(), command.userRole(),
                    new ChatbotAiRequest.Memory(
                            session.getSummary(),
                            toChronologicalMessages(recentMessages),
                            contexts.stream().map(context -> new ChatbotAiRequest.Context(
                                    context.getKind().name(), context.getValue()
                            )).toList()
                    )
            );
        } catch (RuntimeException exception) {
            sessionRepository.releaseStreamLock(session.getSessionId(), requestId);
            throw exception;
        }
    }

    // 현재 요청 ID가 보유한 스트림 락만 해제한다.
    @Transactional
    public void releaseStreamLock(String sessionId, String requestId) {
        sessionRepository.releaseStreamLock(sessionId, requestId);
    }

    private ChatbotSessionJpaEntity resolveSession(String sessionId, Long userId, LocalDateTime now) {
        // 세션 ID가 없으면 현재 사용자 소유의 새 세션을 생성한다.
        if (sessionId == null || sessionId.isBlank()) {
            return sessionRepository.save(ChatbotSessionJpaEntity.create(userId, now));
        }
        // 기존 세션은 존재 여부와 소유권을 모두 검증한다.
        ChatbotSessionJpaEntity session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ChatbotSessionException(ChatbotErrorCode.SESSION_NOT_FOUND));
        if (!session.isOwnedBy(userId)) {
            throw new ChatbotSessionException(ChatbotErrorCode.SESSION_ACCESS_DENIED);
        }
        return session;
    }

    private List<ChatbotAiRequest.Message> toChronologicalMessages(List<ChatbotMessageJpaEntity> messages) {
        // DB의 최신순 조회 결과를 LLM이 이해할 수 있는 시간순으로 되돌린다.
        List<ChatbotMessageJpaEntity> chronological = new ArrayList<>(messages);
        Collections.reverse(chronological);
        return chronological.stream()
                .map(message -> new ChatbotAiRequest.Message(message.getRole().name().toLowerCase(), message.getContent()))
                .toList();
    }

    private void applyQuickReplyIfPresent(
            ChatbotSessionJpaEntity session, SendChatbotMessageCommand command, LocalDateTime now
    ) {
        // 일반 텍스트 요청에는 선택지 컨텍스트를 변경하지 않는다.
        if (command.quickReply() == null) {
            return;
        }
        // FastAPI가 직전에 발급한 선택지 컨텍스트가 있을 때만 값을 반영한다.
        ChatbotContextJpaEntity context = contextRepository.findBySessionIdAndUserIdAndKind(
                        session.getSessionId(), command.userId(), ChatbotContextKind.ROUTINE_PREFERENCE
                )
                .orElseThrow(() -> new ChatbotSessionException(ChatbotErrorCode.INVALID_QUICK_REPLY));
        try {
            RoutinePreferenceContext updated = RoutinePreferenceContext.fromJson(objectMapper, context.getValue())
                    .apply(command.quickReply().questionId(), command.quickReply().value());
            String value = updated.toJson(objectMapper);
            if (value.length() > 500) {
                throw new IllegalArgumentException("Routine preference context exceeds column length");
            }
            context.updateValue(value, now.plusDays(30));
        } catch (IllegalArgumentException exception) {
            throw new ChatbotSessionException(ChatbotErrorCode.INVALID_QUICK_REPLY);
        }
    }
}
