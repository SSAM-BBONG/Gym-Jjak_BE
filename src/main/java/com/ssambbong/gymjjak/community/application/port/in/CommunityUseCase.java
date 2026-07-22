package com.ssambbong.gymjjak.community.application.port.in;

import com.ssambbong.gymjjak.community.application.command.*;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.application.result.CommunityPostListResult;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommunityUseCase {

    Long createCommunityPost(CreateCommunityPostCommand command);

    Page<CommunityPostListResult> findCommunityPosts(
            CommunityPostType type,
            String keyword,
            Pageable pageable
    );

    Page<CommunityPostListResult> findMyCommunityPosts(
            Long userId,
            CommunityPostType type,
            String keyword,
            Pageable pageable
    );

    CommunityPostDetailResult findCommunityPostDetail(
            Long userId,
            Long postId,
            Long commentCursorId,
            int commentSize
    );

    void updateCommunityPost(UpdateCommunityPostCommand command);

    void deleteCommunityPost(DeleteCommunityPostCommand command);

    Long createCommunityComment(CreateCommunityCommentCommand command);

    void updateCommunityComment(UpdateCommunityCommentCommand command);

    void deleteCommunityComment(DeleteCommunityCommentCommand command);

    void createCommunityPostLike(CreateCommunityPostLikeCommand command);

    void deleteCommunityPostLike(DeleteCommunityPostLikeCommand command );
}
