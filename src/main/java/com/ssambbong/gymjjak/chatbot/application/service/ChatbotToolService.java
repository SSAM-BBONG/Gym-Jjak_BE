package com.ssambbong.gymjjak.chatbot.application.service;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotInbodyQueryPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotWorkoutHistoryQueryPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotInbodySnapshot;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotWorkoutHistorySnapshot;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotToolErrorCode;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotToolException;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FastAPI Function Calling 요청을 실제 도메인 조회로 연결하는 응용 서비스입니다.
 *
 * <p>중요: 이 서비스는 FastAPI가 보낸 userId를 받지 않습니다. Spring이 생성한 활성
 * 세션과 요청 ID의 일치 여부를 검증한 뒤, 세션 소유자의 ID를 직접 사용합니다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotToolService {

    private final SpringDataChatbotSessionRepository sessionRepository;
    private final ChatbotInbodyQueryPort inbodyQueryPort;
    private final ChatbotWorkoutHistoryQueryPort workoutHistoryQueryPort;

    /** 최신 인바디 정보를 읽습니다. */
    public ChatbotInbodySnapshot loadLatestInbody(String sessionId, String requestId) {
        return inbodyQueryPort.loadLatest(resolveUserId(sessionId, requestId));
    }

    /** 지정 기간의 운동 일지를 읽습니다. */
    public ChatbotWorkoutHistorySnapshot loadWorkoutHistory(
            String sessionId,
            String requestId,
            LocalDate from,
            LocalDate to
    ) {
        validateDateRange(from, to);
        return workoutHistoryQueryPort.loadHistory(resolveUserId(sessionId, requestId), from, to);
    }

    /**
     * 활성 스트림을 시작한 Spring 서버 요청인지 확인합니다.
     * 이 검증이 있어 LLM/ FastAPI가 임의 userId를 넣어 다른 회원 데이터를 읽을 수 없습니다.
     */
    private Long resolveUserId(String sessionId, String requestId) {
        ChatbotSessionJpaEntity session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ChatbotToolException(ChatbotToolErrorCode.REQUEST_ACCESS_DENIED));

        if (!session.isActiveToolRequest(requestId, LocalDateTime.now())) {
            throw new ChatbotToolException(ChatbotToolErrorCode.REQUEST_ACCESS_DENIED);
        }
        return session.getUserId();
    }

    /** 도구 한 번에 과도한 운동 이력을 읽지 않도록 최대 31일로 제한합니다. */
    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to) || from.plusDays(30).isBefore(to)) {
            throw new ChatbotToolException(ChatbotToolErrorCode.DATE_RANGE_INVALID);
        }
    }
}
