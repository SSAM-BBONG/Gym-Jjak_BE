package com.ssambbong.gymjjak.chat.application.query;

public record ChatMessageQuery(
        Long chatRoomId,
        Long cursor,
        int size
) {}
