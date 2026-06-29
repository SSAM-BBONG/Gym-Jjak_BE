package com.ssambbong.gymjjak.chat;

import com.ssambbong.gymjjak.chat.domain.repository.ChatRoomRepository;
import com.ssambbong.gymjjak.chat.scheduler.application.retention.ChatRetentionProperties;
import com.ssambbong.gymjjak.chat.scheduler.application.retention.ChatRetentionService;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ChatRetentionServiceTest {

    private final ChatRetentionProperties properties =
            new ChatRetentionProperties(90, 500);

    private final ChatRoomRepository chatRoomRepository =
            mock(ChatRoomRepository.class);

    private final ChatRetentionService service =
            new ChatRetentionService(properties, chatRoomRepository);

    @Test
    @DisplayName("보관 기간이 지난 채팅방을 hard delete 한다")
    void hardDeleteExpired_success() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 28, 3, 0);
        LocalDateTime threshold = now.minusDays(90);

        when(chatRoomRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of(1L, 2L));
        when(chatRoomRepository.hardDeleteByIds(List.of(1L, 2L))).thenReturn(2);

        // when
        RetentionJobResult result = service.hardDeleteExpired(now);

        // then
        assertThat(result.jobName()).isEqualTo("chat-retention");
        assertThat(result.candidateCount()).isEqualTo(2);
        assertThat(result.deletedParentCount()).isEqualTo(2);

        verify(chatRoomRepository).hardDeleteByIds(List.of(1L, 2L));
    }

    @Test
    @DisplayName("보관 기간이 지나지 않은 채팅방은 삭제하지 않는다")
    void hardDeleteExpired_empty() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 6, 28, 3, 0);
        LocalDateTime threshold = now.minusDays(90);

        when(chatRoomRepository.findHardDeleteCandidateIds(threshold, 500)).thenReturn(List.of());

        // when
        RetentionJobResult result = service.hardDeleteExpired(now);

        // then
        assertThat(result.jobName()).isEqualTo("chat-retention");
        assertThat(result.candidateCount()).isZero();
        assertThat(result.deletedParentCount()).isZero();

        verify(chatRoomRepository, never()).hardDeleteByIds(anyList());
    }
}
