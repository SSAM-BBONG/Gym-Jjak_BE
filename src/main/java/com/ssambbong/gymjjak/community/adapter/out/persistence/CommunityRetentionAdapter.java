package com.ssambbong.gymjjak.community.adapter.out.persistence;

import com.ssambbong.gymjjak.community.adapter.out.persistence.repository.SpringDataCommunityCommentRepository;
import com.ssambbong.gymjjak.community.adapter.out.persistence.repository.SpringDataCommunityRepository;
import com.ssambbong.gymjjak.community.application.port.out.CommunityRetentionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommunityRetentionAdapter implements CommunityRetentionPort {

    private final SpringDataCommunityRepository communityPostRepository;
    private final SpringDataCommunityCommentRepository communityCommentRepository;

    @Override
    public List<Long> findHardDeleteCandidateCommentIds(LocalDateTime threshold, int batchSize) {
        return communityCommentRepository.findHardDeleteCandidateIds(threshold, batchSize);
    }

    @Override
    public int hardDeleteCommentsByIds(List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return 0;
        }

        return communityCommentRepository.hardDeleteByIds(commentIds);
    }

    @Override
    public List<Long> findHardDeleteCandidatePostIds(LocalDateTime threshold, int batchSize) {
        return communityPostRepository.findHardDeleteCandidateIds(threshold, batchSize);
    }

    @Override
    public int hardDeletePostViewsByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return 0;
        }

        return communityPostRepository.hardDeletePostViewsByPostIds(postIds);
    }

    @Override
    public int hardDeletePostLikesByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return 0;
        }

        return communityPostRepository.hardDeletePostLikesByPostIds(postIds);
    }

    @Override
    public int hardDeleteCommentsByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return 0;
        }

        return communityCommentRepository.hardDeleteByPostIds(postIds);
    }

    @Override
    public int hardDeletePostsByIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return 0;
        }

        return communityPostRepository.hardDeleteByIds(postIds);
    }
}
