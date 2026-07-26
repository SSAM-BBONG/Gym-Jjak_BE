package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out.domain;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotInbodyQueryPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotInbodySnapshot;
import com.ssambbong.gymjjak.inbody.application.query.GetInbodyListQuery;
import com.ssambbong.gymjjak.inbody.application.result.InbodyItemResult;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 챗봇 포트를 인바디 도메인의 공개 조회 UseCase에 연결하는 Adapter입니다.
 * 챗봇은 인바디 Repository나 JPA Entity에 직접 의존하지 않습니다.
 */
@Component
@RequiredArgsConstructor
public class ChatbotInbodyQueryAdapter implements ChatbotInbodyQueryPort {

    private final InbodyQueryUseCase inbodyQueryUseCase;

    @Override
    public ChatbotInbodySnapshot loadLatest(Long userId) {
        return inbodyQueryUseCase.getInbodyList(new GetInbodyListQuery(userId, null, null))
                .inbodies()
                .stream()
                .findFirst()
                .map(this::toSnapshot)
                .orElse(null);
    }

    /** 인바디 전체 모델에서 답변에 필요한 값만 골라 노출합니다. */
    private ChatbotInbodySnapshot toSnapshot(InbodyItemResult inbody) {
        return new ChatbotInbodySnapshot(
                inbody.measuredDate(),
                inbody.weight(),
                inbody.bodyFatPercentage(),
                inbody.skeletalMuscleMass()
        );
    }
}
