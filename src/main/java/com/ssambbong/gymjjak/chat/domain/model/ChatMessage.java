package com.ssambbong.gymjjak.chat.domain.model;

import java.time.LocalDateTime;

public class ChatMessage {

    private final Long id;
    private final Long chatRoomId;
    private final Long senderId;
    private final String content;
    private final boolean read;
    private final LocalDateTime createdAt;

    private ChatMessage(Long id, Long chatRoomId, Long senderId, String content, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static ChatMessage create(Long chatRoomId, Long senderId, String content) {
        return new ChatMessage(null, chatRoomId, senderId, content, false, LocalDateTime.now());
    }

    public static ChatMessage restore(Long id, Long chatRoomId, Long senderId, String content, boolean read, LocalDateTime createdAt) {
        return new ChatMessage(id, chatRoomId, senderId, content, read, createdAt);
    }

    public Long getId() { return id; }
    public Long getChatRoomId() { return chatRoomId; }
    public Long getSenderId() { return senderId; }
    public String getContent() { return content; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
