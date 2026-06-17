package com.ssambbong.gymjjak.chat.application.query;

public record ChatMessageQuery(
        Long chatRoomId,
        Long readerId,
        Long cursor,
        int size
) {}
