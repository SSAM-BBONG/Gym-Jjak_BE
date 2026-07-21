package com.ssambbong.gymjjak.chat.domain.repository;

import com.ssambbong.gymjjak.chat.application.query.ChatRoomSummary;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    Optional<ChatRoom> findById(Long id);
    boolean existsByUserIdAndTrainerProfileIdAndPtCourseIdAndStatus(Long userId, Long trainerProfileId, Long ptCourseId, ChatRoomStatus status);
    void leaveChatRoom(ChatRoom chatRoom);
    List<ChatRoomSummary> findChatRoomsByRequesterId(Long requesterId);
    long countActive();

    long countTotalUnread(Long requesterId);

    List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize);

    int hardDeleteByIds(List<Long> ids);
}
