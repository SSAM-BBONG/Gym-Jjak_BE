package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataChatRoomRepository extends JpaRepository<ChatRoomJpaEntity, Long> {

    boolean existsByUserIdAndTrainerProfileIdAndPtCourseIdAndStatus(Long userId, Long trainerProfileId, Long ptCourseId, ChatRoomStatus status);
    long countByStatus(ChatRoomStatus status);

@Modifying(clearAutomatically = true)
    @Query("UPDATE ChatRoomJpaEntity c SET c.userLeft = true WHERE c.id = :id AND c.status != com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus.DELETED")
    void markUserLeft(@Param("id") Long chatRoomId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatRoomJpaEntity c SET c.trainerLeft = true WHERE c.id = :id AND c.status != com.ssambbong.gymjjak.chat.domain.model.ChatRoomStatus.DELETED")
    void markTrainerLeft(@Param("id") Long chatRoomId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE chat_rooms
            SET status = CASE WHEN user_left = true AND trainer_left = true THEN 'DELETED' ELSE 'CLOSED' END,
                closed_at = COALESCE(closed_at, :closedAt)
            WHERE chat_room_id = :id AND status != 'DELETED'
            """, nativeQuery = true)
    void updateStatusAfterLeave(@Param("id") Long chatRoomId, @Param("closedAt") LocalDateTime closedAt);

    @Query(value = """
            SELECT
                cr.chat_room_id AS chatRoomId,
                CASE WHEN cr.user_id = :requesterId THEN tp.trainer_name ELSE u.nickname END AS partnerName,
                CASE WHEN cr.user_id = :requesterId THEN 'TRAINER' ELSE 'USER' END AS partnerRole,
                CASE WHEN cr.user_id = :requesterId THEN tp.profile_file_id ELSE NULL END AS partnerProfileFileId,
                (SELECT cm.content FROM chat_messages cm
                 WHERE cm.chat_room_id = cr.chat_room_id
                 ORDER BY cm.created_at DESC LIMIT 1) AS lastMessage,
                cr.last_message_at AS lastMessageAt,
                (SELECT COUNT(*) FROM chat_messages cm
                 WHERE cm.chat_room_id = cr.chat_room_id
                   AND cm.sender_id != :requesterId
                   AND cm.is_read = false) AS unreadCount
            FROM chat_rooms cr
            LEFT JOIN trainer_profiles tp ON cr.trainer_profile_id = tp.trainer_profile_id
            LEFT JOIN users u ON cr.user_id = u.user_id
            WHERE (cr.user_id = :requesterId OR tp.user_id = :requesterId)
              AND cr.status != 'DELETED'
              AND NOT (cr.user_id = :requesterId AND cr.user_left = true)
              AND NOT (tp.user_id = :requesterId AND cr.trainer_left = true)
            ORDER BY cr.last_message_at IS NULL ASC, cr.last_message_at DESC
            """, nativeQuery = true)
    List<ChatRoomSummaryProjection> findChatRoomSummariesByRequesterId(@Param("requesterId") Long requesterId);
}
