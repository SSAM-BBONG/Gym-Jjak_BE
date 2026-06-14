package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataChatRoomRepository extends JpaRepository<ChatRoomJpaEntity, Long> {
    boolean existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(Long userId, Long trainerId, Long ptCourseId, ChatRoomStatus status);
}
