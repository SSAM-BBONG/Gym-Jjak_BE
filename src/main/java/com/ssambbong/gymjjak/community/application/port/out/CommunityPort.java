package com.ssambbong.gymjjak.community.application.port.out;

import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.model.CommunityComment;
import com.ssambbong.gymjjak.community.domain.model.CommunityPost;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommunityPort {
    Long saveCommunityPost(CommunityPost communityPost);

    Page<CommunityPostListResult> findCommunityPosts(
            CommunityPostType type,
            Pageable pageable
    );

    boolean existsCommunityPost(Long postId);

    Optional<CommunityPostDetailResult> findCommunityPostDetail(
            Long postId,
            Long userId,
            Long commentCursorId,
            int commentSize
    );

    boolean saveViewIfAbsent(
            Long postId,
            Long userId
    );

    void increaseViewCount(Long postId);

    Optional<CommunityPost> findCommunityPostById(
            Long postId
    );

    void updateCommunityPost(CommunityPost communityPost);

    void deleteCommunityPost(Long postId);

    Long saveCommunityComment(CommunityComment communityComment);

    Optional<CommunityComment> findCommunityCommentById(Long commentId);

    void updateCommunityComment(CommunityComment communityComment);

    void deleteCommunityComment(Long commentId);
}
