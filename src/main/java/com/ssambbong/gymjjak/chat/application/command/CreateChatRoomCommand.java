package com.ssambbong.gymjjak.chat.application.command;

public record CreateChatRoomCommand(
        Long userId,
        Long trainerId,
        Long ptCourseId
) {
}
