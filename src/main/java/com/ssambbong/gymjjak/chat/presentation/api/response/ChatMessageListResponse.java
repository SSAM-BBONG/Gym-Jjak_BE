package com.ssambbong.gymjjak.chat.presentation.api.response;

import java.util.List;

public record ChatMessageListResponse(
        List<ChatMessageResponse> messages,
        Long nextCursor,
        boolean hasNext
) {}
