package com.ssambbong.gymjjak.chat.application.command;

public record SendChatMessageCommand(
        Long chatRoomId,
        Long senderId,
        String content
) {}
