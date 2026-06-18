package com.ssambbong.gymjjak.chat.presentation.api.response;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;

import java.util.List;

public record ChatMessageListResponse(
        List<ChatMessageResponse> messages,
        Long nextCursor,
        boolean hasNext
) {
    public static ChatMessageListResponse from(ChatMessageListResult result) {
        return new ChatMessageListResponse(
                result.messages().stream().map(ChatMessageResponse::from).toList(),
                result.nextCursor(),
                result.hasNext()
        );
    }
}
