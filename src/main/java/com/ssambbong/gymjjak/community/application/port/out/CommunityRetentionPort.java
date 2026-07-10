package com.ssambbong.gymjjak.community.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

public interface CommunityRetentionPort {

    List<Long> findHardDeleteCandidateCommentIds(LocalDateTime threshold, int batchSize);

    int hardDeleteCommentsByIds(List<Long> commentIds);

    List<Long> findHardDeleteCandidatePostIds(LocalDateTime threshold, int batchSize);

    int hardDeletePostViewsByPostIds(List<Long> postIds);

    int hardDeletePostLikesByPostIds(List<Long> postIds);

    int hardDeleteCommentsByPostIds(List<Long> postIds);

    int hardDeletePostsByIds(List<Long> postIds);
}
