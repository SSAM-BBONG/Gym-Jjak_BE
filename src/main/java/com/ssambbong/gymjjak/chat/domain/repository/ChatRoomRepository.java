package com.ssambbong.gymjjak.chat.domain.repository;

import com.ssambbong.gymjjak.chat.application.query.ChatRoomSummary;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoom;
import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    Optional<ChatRoom> findById(Long id);
    Optional<ChatRoom> findByIdAndParticipant(Long chatRoomId, Long userId);
    boolean existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(Long userId, Long trainerId, Long ptCourseId, ChatRoomStatus status);
    void leaveChatRoom(ChatRoom chatRoom);
    List<ChatRoomSummary> findChatRoomsByRequesterId(Long requesterId);
}
