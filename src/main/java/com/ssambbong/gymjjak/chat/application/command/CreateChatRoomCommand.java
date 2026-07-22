package com.ssambbong.gymjjak.chat.application.command;

public record CreateChatRoomCommand(
        Long requesterId,
        Long userId,
        Long ptCourseId
) {
}
