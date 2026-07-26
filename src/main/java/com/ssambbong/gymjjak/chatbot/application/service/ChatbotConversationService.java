package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.model.ChatbotQuickReply;
import com.ssambbong.gymjjak.chatbot.application.model.RoutinePreferenceContext;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationPreparation;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationStart;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotContextKind;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotContextJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotMessageJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotContextRepository;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotMessageRepository;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotConversationService {

    private final ChatbotConversationPreparationService preparationService;
    private final ChatbotUserDataSnapshotService snapshotService;
    private final SpringDataChatbotSessionRepository sessionRepository;
    private final SpringDataChatbotMessageRepository messageRepository;
    private final SpringDataChatbotContextRepository contextRepository;
    private final ObjectMapper objectMapper;

    // 짧은 쓰기 트랜잭션을 먼저 끝낸 뒤, 별도 읽기 전용 트랜잭션에서 스냅샷을 조회한다.
    public ChatbotConversationStart prepare(SendChatbotMessageCommand command) {
        // 락 획득과 USER 메시지 저장이 커밋된 상태를 먼저 확보한다.
        ChatbotConversationPreparation preparation = preparationService.prepare(command);
        try {
            // 운동 기록 집계가 포함된 개인 데이터는 락 트랜잭션 밖에서 조회한다.
            return preparation.toStart(command.userId(), snapshotService.load(command.userId()));
        } catch (RuntimeException exception) {
            // 준비 트랜잭션은 이미 커밋됐으므로, 스냅샷 실패 시 현재 요청의 락을 직접 해제한다.
            preparationService.releaseStreamLock(preparation.sessionId(), preparation.requestId());
            throw exception;
        }
    }

    // FastAPI의 최종 완료 이벤트만 assistant 메시지와 선택지 컨텍스트로 저장한다.
    @Transactional
    public void persistDone(ChatbotConversationStart start, ChatbotAiEvent.Done done) {
        // FastAPI 응답이 요청한 세션의 응답인지 확인한다.
        if (!start.sessionId().equals(done.sessionId())) {
            throw new ChatbotSessionException(ChatbotErrorCode.FASTAPI_RESPONSE_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();
        // 스트리밍 중간 조각이 아닌 최종 응답만 이력에 저장한다.
        messageRepository.save(ChatbotMessageJpaEntity.assistant(
                start.sessionId(), done.answer(), done.category(), done.routineJson(),
                done.sourcesJson(), done.limited()
        ));
        persistRoutineQuickReplies(
                start.sessionId(), start.fastApiRequest().actor().userId(), done.quickRepliesJson(), now
        );
        sessionRepository.findBySessionId(start.sessionId()).ifPresent(session -> session.touch(now));
    }

    // WebSocket 실행 종료 시 준비 단계에서 획득한 락을 해제한다.
    public void releaseStreamLock(ChatbotConversationStart start) {
        preparationService.releaseStreamLock(start.sessionId(), start.requestId());
    }

    private void persistRoutineQuickReplies(String sessionId, Long userId, String quickRepliesJson, LocalDateTime now) {
        // FastAPI가 반환한 루틴 선택지만 다음 요청에서 검증할 수 있도록 저장한다.
        try {
            List<ChatbotQuickReply> replies = objectMapper.readValue(
                    quickRepliesJson, new TypeReference<List<ChatbotQuickReply>>() {
                    }
            );
            // 루틴과 무관한 선택지는 저장하지 않는다.
            if (replies.stream().anyMatch(reply -> !reply.questionId().startsWith("ROUTINE_"))) {
                return;
            }
            ChatbotContextJpaEntity context = contextRepository.findBySessionIdAndUserIdAndKind(
                            sessionId, userId, ChatbotContextKind.ROUTINE_PREFERENCE
                    )
                    .orElseGet(() -> new ChatbotContextJpaEntity(
                            sessionId, userId, ChatbotContextKind.ROUTINE_PREFERENCE,
                            RoutinePreferenceContext.empty().toJson(objectMapper), now.plusDays(30)
                    ));
            RoutinePreferenceContext updated = RoutinePreferenceContext.fromJson(objectMapper, context.getValue())
                    .withPendingQuickReplies(replies);
            String value = updated.toJson(objectMapper);
            if (value.length() > 500) {
                throw new IllegalArgumentException("Routine preference context exceeds column length");
            }
            context.updateValue(value, now.plusDays(30));
            contextRepository.save(context);
        } catch (IllegalArgumentException | java.io.IOException exception) {
            throw new ChatbotSessionException(ChatbotErrorCode.FASTAPI_RESPONSE_INVALID);
        }
    }
}
