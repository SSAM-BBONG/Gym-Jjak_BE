package com.ssambbong.gymjjak.chatbot.application.service;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshot;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshotPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatbotUserDataSnapshotService {
    private final ChatbotUserDataSnapshotPort userDataSnapshotPort;

    // 스트림 락 트랜잭션이 커밋된 뒤 AI 요청용 개인 데이터 스냅샷을 읽는다.
    @Transactional(readOnly = true)
    public ChatbotUserDataSnapshot load(Long userId) {
        return userDataSnapshotPort.load(userId);
    }
}
