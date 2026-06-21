package com.ssambbong.gymjjak.chat.presentation.api.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long senderId,
        String content,
        Boolean read,
        LocalDateTime createdAt
) {}
