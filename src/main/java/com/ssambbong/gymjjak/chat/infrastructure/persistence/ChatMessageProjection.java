package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import java.time.LocalDateTime;

public interface ChatMessageProjection {
    Long getChatMessageId();
    Long getSenderId();
    String getContent();
    Boolean getIsRead();
    LocalDateTime getCreatedAt();
}
