package com.ssambbong.gymjjak.chat.scheduler.application.retention;

import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRetentionService {

    public static final String JOB_NAME = "chat-retention";

    private final ChatRetentionProperties properties;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpired(LocalDateTime now) {
        int[] result = hardDeleteExpiredChatRooms(now);
        return new RetentionJobResult(JOB_NAME, result[0], 0, result[1]);
    }

    private int[] hardDeleteExpiredChatRooms(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);
        List<Long> candidateIds = chatRoomRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=chat-room-retention-empty threshold={}", threshold);
            return new int[]{0, 0};
        }

        int deleted = chatRoomRepository.hardDeleteByIds(candidateIds);
        log.info("event=chat-room-retention-completed threshold={}, candidateCount={}, deleted={}", threshold, candidateIds.size(), deleted);
        return new int[]{candidateIds.size(), deleted};
    }
}
