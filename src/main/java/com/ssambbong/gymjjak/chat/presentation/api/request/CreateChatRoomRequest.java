package com.ssambbong.gymjjak.chat.presentation.api.request;

import jakarta.validation.constraints.NotNull;

public record CreateChatRoomRequest(
        @NotNull Long trainerId,
        @NotNull Long ptCourseId
) {
}
