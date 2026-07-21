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
                closed_at = COALESCE(closed_at, :closedAt),
                deleted_at = CASE WHEN user_left = true AND trainer_left = true THEN :closedAt ELSE NULL END
            WHERE chat_room_id = :id AND status != 'DELETED'
            """, nativeQuery = true)
    void updateStatusAfterLeave(@Param("id") Long chatRoomId, @Param("closedAt") LocalDateTime closedAt);

    @Query(value = """
            SELECT
                cr.chat_room_id AS chatRoomId,
                CASE WHEN cr.user_id = :requesterId THEN tp.trainer_name ELSE u.nickname END AS partnerName,
                CASE WHEN cr.user_id = :requesterId THEN 'TRAINER' ELSE 'USER' END AS partnerRole,
                CASE WHEN cr.user_id = :requesterId THEN tp.profile_file_id ELSE NULL END AS partnerProfileFileId,
                last_cm.content AS lastMessage,
                cr.last_message_at AS lastMessageAt,
                COALESCE(unread.cnt, 0) AS unreadCount
            FROM chat_rooms cr
            LEFT JOIN trainer_profiles tp ON cr.trainer_profile_id = tp.trainer_profile_id
            LEFT JOIN users u ON cr.user_id = u.user_id
            LEFT JOIN (
                SELECT chat_room_id, MAX(chat_message_id) AS max_id
                FROM chat_messages
                WHERE chat_room_id IN (
                    SELECT cr2.chat_room_id FROM chat_rooms cr2
                    LEFT JOIN trainer_profiles tp2 ON cr2.trainer_profile_id = tp2.trainer_profile_id
                    WHERE (cr2.user_id = :requesterId OR tp2.user_id = :requesterId)
                      AND cr2.status != 'DELETED'
                )
                GROUP BY chat_room_id
            ) AS last_ids ON last_ids.chat_room_id = cr.chat_room_id
            LEFT JOIN chat_messages last_cm ON last_cm.chat_message_id = last_ids.max_id
            LEFT JOIN (
                SELECT chat_room_id, COUNT(*) AS cnt
                FROM chat_messages
                WHERE chat_room_id IN (
                    SELECT cr2.chat_room_id FROM chat_rooms cr2
                    LEFT JOIN trainer_profiles tp2 ON cr2.trainer_profile_id = tp2.trainer_profile_id
                    WHERE (cr2.user_id = :requesterId OR tp2.user_id = :requesterId)
                      AND cr2.status != 'DELETED'
                )
                  AND sender_id != :requesterId
                  AND is_read = false
                GROUP BY chat_room_id
            ) AS unread ON unread.chat_room_id = cr.chat_room_id
            WHERE (cr.user_id = :requesterId OR tp.user_id = :requesterId)
              AND cr.status != 'DELETED'
              AND NOT (cr.user_id = :requesterId AND cr.user_left = true)
              AND NOT (tp.user_id = :requesterId AND cr.trainer_left = true)
            ORDER BY cr.last_message_at IS NULL ASC, cr.last_message_at DESC
            """, nativeQuery = true)
    List<ChatRoomSummaryProjection> findChatRoomSummariesByRequesterId(@Param("requesterId") Long requesterId);

    @Query(value = """
            SELECT COALESCE(SUM(cnt), 0)
            FROM (
                SELECT COUNT(*) AS cnt
                FROM chat_messages cm
                JOIN chat_rooms cr ON cm.chat_room_id = cr.chat_room_id
                LEFT JOIN trainer_profiles tp ON cr.trainer_profile_id = tp.trainer_profile_id
                WHERE (cr.user_id = :requesterId OR tp.user_id = :requesterId)
                  AND cr.status != 'DELETED'
                  AND NOT (cr.user_id = :requesterId AND cr.user_left = true)
                  AND NOT (tp.user_id = :requesterId AND cr.trainer_left = true)
                  AND cm.sender_id != :requesterId
                  AND cm.is_read = false
                GROUP BY cm.chat_room_id
            ) AS t
            """, nativeQuery = true)
    long countTotalUnreadByRequesterId(@Param("requesterId") Long requesterId);

    @Query(value = "SELECT chat_room_id FROM chat_rooms WHERE deleted_at IS NOT NULL AND deleted_at < :threshold ORDER BY deleted_at ASC, chat_room_id ASC LIMIT :batchSize", nativeQuery = true)
    List<Long> findHardDeleteCandidateIds(@Param("threshold") LocalDateTime threshold, @Param("batchSize") int batchSize);

    @Modifying
    @Query(value = "DELETE FROM chat_rooms WHERE chat_room_id IN :ids", nativeQuery = true)
    int hardDeleteByIds(@Param("ids") List<Long> ids);
}
