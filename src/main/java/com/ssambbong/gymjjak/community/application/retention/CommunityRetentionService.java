package com.ssambbong.gymjjak.community.application.retention;

import com.ssambbong.gymjjak.community.application.port.out.CommunityRetentionPort;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityRetentionService {

    public static final String JOB_NAME = "community-retention";

    private final CommunityRetentionProperties properties;
    private final CommunityRetentionPort communityRetentionPort;

    @Transactional
    public RetentionJobResult hardDeleteExpired(LocalDateTime now) {
        CommentDeletionResult comments = hardDeleteExpiredComments(now);
        PostDeletionResult posts = hardDeleteExpiredPosts(now);

        return new RetentionJobResult(
                JOB_NAME,
                comments.candidateCount() + posts.candidateCount(),
                comments.deletedCount() + posts.deletedChildCount(),
                posts.deletedPostCount()
        );
    }

    private CommentDeletionResult hardDeleteExpiredComments(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);
        List<Long> candidateIds =
                communityRetentionPort.findHardDeleteCandidateCommentIds(
                        threshold,
                        properties.batchSize()
                );

        if (candidateIds.isEmpty()) {
            log.info("event=community-comment-retention-empty threshold={}", threshold);
            return new CommentDeletionResult(0, 0);
        }

        int deletedComments = communityRetentionPort.hardDeleteCommentsByIds(candidateIds);
        log.info(
                "event=community-comment-retention-completed threshold={}, candidateCount={}, deletedComments={}",
                threshold,
                candidateIds.size(),
                deletedComments
        );

        return new CommentDeletionResult(candidateIds.size(), deletedComments);
    }

    private PostDeletionResult hardDeleteExpiredPosts(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);
        List<Long> candidateIds =
                communityRetentionPort.findHardDeleteCandidatePostIds(
                        threshold,
                        properties.batchSize()
                );

        if (candidateIds.isEmpty()) {
            log.info("event=community-post-retention-empty threshold={}", threshold);
            return new PostDeletionResult(0, 0, 0);
        }

        int deletedViews = communityRetentionPort.hardDeletePostViewsByPostIds(candidateIds);
        int deletedLikes = communityRetentionPort.hardDeletePostLikesByPostIds(candidateIds);
        int deletedComments = communityRetentionPort.hardDeleteCommentsByPostIds(candidateIds);
        int deletedPosts = communityRetentionPort.hardDeletePostsByIds(candidateIds);

        log.info(
                "event=community-post-retention-completed threshold={}, candidateCount={}, deletedViews={}, deletedLikes={}, deletedComments={}, deletedPosts={}",
                threshold,
                candidateIds.size(),
                deletedViews,
                deletedLikes,
                deletedComments,
                deletedPosts
        );

        return new PostDeletionResult(
                candidateIds.size(),
                deletedViews + deletedLikes + deletedComments,
                deletedPosts
        );
    }

    private record CommentDeletionResult(
            int candidateCount,
            int deletedCount
    ) {
    }

    private record PostDeletionResult(
            int candidateCount,
            int deletedChildCount,
            int deletedPostCount
    ) {
    }
}
