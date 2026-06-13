package com.ssambbong.gymjjak.chat.domain.repository;

import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;

import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    Optional<ChatRoom> findById(Long id);
    boolean existsByUserIdAndTrainerProfileIdAndStatus(Long userId, Long trainerProfileId, ChatRoomStatus status);
}
