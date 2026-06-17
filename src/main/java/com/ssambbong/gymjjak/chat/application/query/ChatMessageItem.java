package com.ssambbong.gymjjak.chat.application.query;

import java.time.LocalDateTime;

public record ChatMessageItem(
        Long messageId,
        Long senderId,
        String content,
        Boolean read,
        LocalDateTime createdAt
) {}
