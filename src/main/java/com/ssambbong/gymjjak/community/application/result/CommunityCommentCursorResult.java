package com.ssambbong.gymjjak.community.application.result;

import java.util.List;

public record CommunityCommentCursorResult(
        List<CommunityCommentResult> content,
        Long nextCursorId,
        boolean hasNext
) {
}
