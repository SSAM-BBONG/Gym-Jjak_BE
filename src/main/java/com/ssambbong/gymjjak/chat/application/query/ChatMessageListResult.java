package com.ssambbong.gymjjak.chat.application.query;

import java.util.List;

public record ChatMessageListResult(
        List<ChatMessageItem> messages,
        Long nextCursor,
        boolean hasNext
) {}
