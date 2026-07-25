package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.model.ChatbotQuickReply;
import com.ssambbong.gymjjak.chatbot.application.model.RoutinePreferenceContext;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotSubscriptionAccessPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationStart;
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
public class ChatbotConversationService {

    private static final int STREAM_LOCK_SECONDS = 120;

    private final SpringDataChatbotSessionRepository sessionRepository;
    private final SpringDataChatbotMessageRepository messageRepository;
    private final SpringDataChatbotContextRepository contextRepository;
    private final ChatbotSubscriptionAccessPort subscriptionAccessPort;
    private final ObjectMapper objectMapper;

    /**
     * STOMP 메시지를 FastAPI 요청으로 바꾸기 전의 영속화와 동시성 제어를 담당합니다.
     * 현재 질문은 저장하되, FastAPI에는 저장 전의 최근 대화만 메모리로 전달합니다.
     */
    @Transactional
    public ChatbotConversationStart prepare(SendChatbotMessageCommand command) {

        // 구독권 검증
        if (!subscriptionAccessPort.hasActiveAccess(command.userId())) {
            throw new ChatbotSessionException(ChatbotErrorCode.SUBSCRIPTION_REQUIRED);
        }
        LocalDateTime now = LocalDateTime.now();
        // 세선 생성 및 소유권 검증
        ChatbotSessionJpaEntity session = resolveSession(command.sessionId(), command.userId(), now);
        // 최근 12개 메시지 조회
        List<ChatbotMessageJpaEntity> recentMessages = messageRepository
                .findTop12BySessionIdOrderByCreatedAtDesc(session.getSessionId());

        // 이번 요청 고유 uuid
        String requestId = UUID.randomUUID().toString();

        // 해당 요청 세션에 낙관적 락 걸기
        // 동시성 제어, DB 레벨에서 잠구고, 120초 타임아웃
        int updated = sessionRepository.acquireStreamLock(
                session.getSessionId(),
                command.userId(),
                requestId,
                now.plusSeconds(STREAM_LOCK_SECONDS), // 120초 후 만료
                now
        );
        if (updated == 0) {
            throw new ChatbotSessionException(ChatbotErrorCode.STREAM_IN_PROGRESS);
        }

        try {
            applyQuickReplyIfPresent(session, command, now);
            // 사용자 채팅 저장, DB에 저장, 아직 commit 안됨
            messageRepository.save(ChatbotMessageJpaEntity.user(
                    session.getSessionId(), command.content(), command.intentHint()
            ));
            // 마지막 행동 갱신
            session.touch(now);
            //
            sessionRepository.save(session);
            List<ChatbotContextJpaEntity> contexts = contextRepository.findActiveBySessionIdAndUserId(
                    session.getSessionId(), command.userId(), now
            );
            // 하나의 객체로 fastapi 요청 객체 생성
            return new ChatbotConversationStart(
                    session.getSessionId(),
                    requestId,
                    // 실제 fastapi 전달 값
                    new ChatbotAiRequest(
                            session.getSessionId(), command.content(), command.intentHint(),
                            // 주체 확인
                            new ChatbotAiRequest.Actor(command.userId(), command.userRole()),
                            // 대화 이력 요약, 12개 조회
                            new ChatbotAiRequest.Memory(
                                    session.getSummary(),
                                    // 최신순 -> 오래된 순으로 역순 정렬
                                    toChronologicalMessages(recentMessages),
                                    // 컨텍스트 정보들 pt, 신체 정보
                                    contexts.stream()
                                            .map(context ->
                                                    new ChatbotAiRequest.Context(
                                                            context.getKind().name(), // 정보 종류
                                                            context.getValue() // 정보의 내용
                                                    )
                                            )
                                            .toList()
                            ),
                            // 추적용 requestId
                            requestId
                    )
            );
        } catch (RuntimeException exception) {
            sessionRepository.releaseStreamLock(session.getSessionId(), requestId);
            throw exception;
        }
    }

    // FastAPI의 done 이벤트 저장
    // ai 메시지와 UI 복원용 메타데이터로 저장
    @Transactional
    public void persistDone(ChatbotConversationStart start, ChatbotAiEvent.Done done) {
        // 세션 검증
        if (!start.sessionId().equals(done.sessionId())) {
            throw new ChatbotSessionException(ChatbotErrorCode.FASTAPI_RESPONSE_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();

        // assistant 메시지 저장
        messageRepository.save(ChatbotMessageJpaEntity.assistant(
                start.sessionId(),
                done.answer(),  // 최종 ai 답변
                done.category(),    // 카테고리 : PT_RECOMMENDATION
                done.routineJson(), // 루틴 정보
                done.sourcesJson(), // 출처
                done.limited()  // 제한 여부
        ));
        // quick reply 저장, 사용자 선호도 저장
        persistRoutineQuickReplies(start.sessionId(), start.fastApiRequest().actor().userId(), done.quickRepliesJson(), now);
        // 세션 갱신
        sessionRepository.findBySessionId(start.sessionId()).ifPresent(session -> session.touch(now));
    }

    // 스트림 종료 성공 실패 여부와 무관하게 현재 잠긴 요청의 락을 해제
    @Transactional
    public void releaseStreamLock(ChatbotConversationStart start) {
        sessionRepository.releaseStreamLock(start.sessionId(), start.requestId());
    }

    // 세션이 없으면 생성하고, 있으면 현재 사용자 소유인지 검증합니다.
    private ChatbotSessionJpaEntity resolveSession(String sessionId, Long userId,
                                                   LocalDateTime now) {
        if (sessionId == null || sessionId.isBlank()) {
            // sessionId가 없으면 새 세션 생성
            return sessionRepository.save(ChatbotSessionJpaEntity.create(userId, now));
        }
        // sessionId 있으면 존재 여부 확인
        ChatbotSessionJpaEntity session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ChatbotSessionException(ChatbotErrorCode.SESSION_NOT_FOUND));
        // 세션 소유권 검증 - 채팅방 분리 목적
        if (!session.isOwnedBy(userId)) {
            throw new ChatbotSessionException(ChatbotErrorCode.SESSION_ACCESS_DENIED);
        }
        return session;
    }

    // DB의 최신순 메시지를 LLM 대화 순서인 오래된 순으로 되돌립니다.
    private List<ChatbotAiRequest.Message>
        toChronologicalMessages(List<ChatbotMessageJpaEntity> messages) {
            // 메시지 List 생성
            List<ChatbotMessageJpaEntity> chronological = new ArrayList<>(messages);
            // 오래된 순으로 정렬
            Collections.reverse(chronological);
            // 사용자 추가 정보를 모아 마지막에 ai 서버로 호출
            return chronological.stream()
                    .map(message -> new ChatbotAiRequest.Message(
                            message.getRole().name().toLowerCase(), // assistant, user
                            message.getContent()    // 대화 내용
                    ))
                    .toList();
    }

    private void applyQuickReplyIfPresent(
            ChatbotSessionJpaEntity session, SendChatbotMessageCommand command, LocalDateTime now
    ) {
        if (command.quickReply() == null) {
            return;
        }
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

    private void persistRoutineQuickReplies(String sessionId, Long userId, String quickRepliesJson, LocalDateTime now) {
        try {
            List<ChatbotQuickReply> replies = objectMapper.readValue(
                    quickRepliesJson, new TypeReference<List<ChatbotQuickReply>>() {
                    }
            );
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
