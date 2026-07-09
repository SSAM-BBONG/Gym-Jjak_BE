package com.ssambbong.gymjjak.chat.infrastructure.adapter;

import com.ssambbong.gymjjak.chat.domain.repository.ChatMessageRepository;
import com.ssambbong.gymjjak.chat.exception.ChatMessageNotFoundException;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.chat.ChatSanctionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSanctionAdapter implements ChatSanctionPort {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional(readOnly = true)
    public void applySanction(Long targetId, ReportSanctionAction action) {
        chatMessageRepository.findById(targetId)
                .orElseThrow(ChatMessageNotFoundException::new);

        log.info("[ChatSanction] 채팅 메시지 제재 요청 확인 - chatMessageId={}, action={}", targetId, action);
    }
}
