package com.ssambbong.gymjjak.user.application.result;

import java.util.List;

public record CursorResult<T>(
        List<T> content,
        Long nextCursor,
        boolean hasNext
) {
}
