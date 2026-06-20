package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataChatMessageRepository extends JpaRepository<ChatMessageJpaEntity, Long> {

    @Query(value = """
            SELECT
                cm.chat_message_id AS chatMessageId,
                cm.sender_id       AS senderId,
                cm.content         AS content,
                cm.is_read         AS isRead,
                cm.created_at      AS createdAt
            FROM chat_messages cm
            WHERE cm.chat_room_id = :chatRoomId
            ORDER BY cm.chat_message_id DESC
            LIMIT :size
            """, nativeQuery = true)
    List<ChatMessageProjection> findLatestMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("size") int size
    );

    @Query(value = """
            SELECT
                cm.chat_message_id AS chatMessageId,
                cm.sender_id       AS senderId,
                cm.content         AS content,
                cm.is_read         AS isRead,
                cm.created_at      AS createdAt
            FROM chat_messages cm
            WHERE cm.chat_room_id = :chatRoomId
              AND cm.chat_message_id < :cursor
            ORDER BY cm.chat_message_id DESC
            LIMIT :size
            """, nativeQuery = true)
    List<ChatMessageProjection> findMessagesBeforeCursor(
            @Param("chatRoomId") Long chatRoomId,
            @Param("cursor") Long cursor,
            @Param("size") int size
    );

    @Modifying
    @Query(value = """
            UPDATE chat_messages
            SET is_read = true
            WHERE chat_room_id = :chatRoomId
              AND sender_id != :readerId
              AND is_read = false
            """, nativeQuery = true)
    void markMessagesAsRead(@Param("chatRoomId") Long chatRoomId, @Param("readerId") Long readerId);

    @Modifying
    @Query(value = "UPDATE chat_messages SET is_read = true WHERE chat_message_id = :messageId", nativeQuery = true)
    void markAsRead(@Param("messageId") Long messageId);
}
