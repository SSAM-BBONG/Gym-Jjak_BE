package com.ssambbong.gymjjak.chat.infrastructure.adapter;

import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;
import com.ssambbong.gymjjak.chat.domain.repository.ChatMessageRepository;
import com.ssambbong.gymjjak.chat.exception.ChatMessageNotFoundException;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.port.chat.ChatReportTargetPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatReportTargetAdapter implements ChatReportTargetPort {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.debug("[ChatSnapshot] chatMessageId={}", targetId);

        ChatMessage chatMessage = chatMessageRepository.findById(targetId)
                .orElseThrow(ChatMessageNotFoundException::new);

        return new ReportTargetSnapshot(
                chatMessage.getId(),
                chatMessage.getSenderId(),
                "채팅 메시지",
                chatMessage.getContent(),
                null
        );
    }
}
