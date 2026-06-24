package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private ChatMessageJpaEntity(Long chatRoomId, Long senderId, String content, boolean read, LocalDateTime createdAt) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static ChatMessageJpaEntity from(ChatMessage message) {
        return new ChatMessageJpaEntity(
                message.getChatRoomId(),
                message.getSenderId(),
                message.getContent(),
                message.isRead(),
                message.getCreatedAt()
        );
    }

    public ChatMessage toDomain() {
        return ChatMessage.restore(id, chatRoomId, senderId, content, read, createdAt);
    }
}
