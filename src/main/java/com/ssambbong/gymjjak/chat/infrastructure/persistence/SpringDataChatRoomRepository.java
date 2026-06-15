package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SpringDataChatRoomRepository extends JpaRepository<ChatRoomJpaEntity, Long> {

    boolean existsByUserIdAndTrainerIdAndPtCourseIdAndStatus(Long userId, Long trainerId, Long ptCourseId, ChatRoomStatus status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatRoomJpaEntity c SET c.userLeft = true WHERE c.id = :id")
    void markUserLeft(@Param("id") Long chatRoomId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatRoomJpaEntity c SET c.trainerLeft = true WHERE c.id = :id")
    void markTrainerLeft(@Param("id") Long chatRoomId);

    @Modifying
    @Query(value = """
            UPDATE chat_rooms
            SET status = CASE WHEN user_left = true AND trainer_left = true THEN 'DELETED' ELSE 'CLOSED' END,
                closed_at = COALESCE(closed_at, :closedAt)
            WHERE chat_room_id = :id
            """, nativeQuery = true)
    void updateStatusAfterLeave(@Param("id") Long chatRoomId, @Param("closedAt") LocalDateTime closedAt);
}
