package com.ssambbong.gymjjak.chat.presentation.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateChatRoomRequest(
        @NotNull @Positive Long ptCourseId
) {
}
