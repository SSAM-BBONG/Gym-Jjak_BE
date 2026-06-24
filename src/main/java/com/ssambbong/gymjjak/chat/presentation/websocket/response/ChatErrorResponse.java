package com.ssambbong.gymjjak.chat.presentation.websocket.response;

import java.time.LocalDateTime;

public record ChatErrorResponse(
        LocalDateTime timestamp,
        String code,
        String message
) {}
